"""
views.py

@author Jason McMullen, Amit Mondal, Tyler Russell, Huy Vuong, Long Pham
"""

from django.shortcuts import render
from django.http import HttpResponseRedirect
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.decorators import login_required
from django.contrib.auth.models import User

from django.core.exceptions import ObjectDoesNotExist

from django.shortcuts import render_to_response
from django.template import RequestContext
from health.forms import RegistrationForm, LoginForm, ProfileForm, AdminForm, SearchForm, DoctorProfileForm
from health.forms import AppointmentFormDoctor, AppointmentFormPatient, AppointmentFormNurse, QueryForm, PrescriptionForm
from health.models import Appointment, Patient, Doctor, Administrator, Nurse, ActivityLog, Prescription
from datetime import datetime

"""
Register a new patient to the system
"""
def PatientRegistration(request):

    # If already logged in, do not let the user reregister
    if request.user.is_authenticated():
        return HttpResponseRedirect('/profile/')

    # If the user submits a form
    if request.method == 'POST':
        form = RegistrationForm(request.POST)

        # Pass form back through all the clean methods we had created and make new user
        if form.is_valid():
            user = User.objects.create_user(username=form.cleaned_data['username'],
                                                                       email = form.cleaned_data['email'],
                                                                       password=form.cleaned_data['password'])
            user.save()

            # Save all of the gathered information into a patient object
            patient = Patient(user=user,
                              first_name=form.cleaned_data['first_name'],
                              mid_initial=form.cleaned_data['mid_initial'],
                              last_name=form.cleaned_data['last_name'],
                              appt_num=form.cleaned_data['appt_num'],
                              address=form.cleaned_data['address'],
                              city=form.cleaned_data['city'],
                              state=form.cleaned_data['state'],
                              zipcode=form.cleaned_data['zipcode'],
                              dob=form.cleaned_data['dob'],
                              phone=form.cleaned_data['phone'],
                              ins_name=form.cleaned_data['ins_name'],
                              ins_num=form.cleaned_data['ins_num'],
                              ins_phone=form.cleaned_data['ins_phone'],
                              pref_hospital=form.cleaned_data['pref_hospital'],
                              emerg_first_name=form.cleaned_data['emerg_first_name'],
                              emerg_last_name=form.cleaned_data['emerg_last_name'],
                              emerg_phone=form.cleaned_data['emerg_phone'],
                              doctor=form.cleaned_data['doctor'],
                             )

            patient.save()

            # Auto sign in user
            patient = authenticate(username=form.cleaned_data['username'],
                                   password=form.cleaned_data['password'])
            login(request, patient)

            # Log this registration activity.
            newLog = ActivityLog(user=user.username,time_logged=datetime.now(),message=user.username + ' registered.')
            newLog.save()

            # Send the user to their profile page
            return HttpResponseRedirect('/profile/')
        else:
            # Send the form that is not valid back to register.html and display the errors to be fixed
            return render_to_response('register.html', {'form': form}, context_instance=RequestContext(request))

    else:
        # Send the form that is not valid back to register.html and display the errors to be fixed
        form = RegistrationForm()
        context = {'form' : form}
        return render_to_response('register.html', context, context_instance=RequestContext(request))

"""
Handle login requests
"""
def LoginRequest(request):

    # Check if already logged in - if so send to profile instead of login page
    if request.user.is_authenticated():
        return HttpResponseRedirect('/profile/')

    # If user is not logged in, gather information entered in login page
    if request.method == 'POST':

        form = LoginForm(request.POST)

        # If the form is valid, gather the information on the page and process it
        if form.is_valid():
            username = form.cleaned_data['username']
            password = form.cleaned_data['password']

            # This step is logging the user in
            user = authenticate(username=username, password=password)

            if user is not None:
                if user.is_active:
                    login(request, user)
                else:
                    return HttpResponseRedirect('/')

                # log login activity if not superuser
                if not user.is_superuser:
                    newLog = ActivityLog(user=request.user.username,time_logged=datetime.now(),message=request.user.username + ' logged in')
                    newLog.save()
                return HttpResponseRedirect('/profile/')
            else:
                return render_to_response('login.html', {'form': form }, context_instance=RequestContext(request))

        # Something wrong with the form, send them back to make corrections
        else:
            return render_to_response('login.html', {'form': form }, context_instance=RequestContext(request))
    else:
        form = LoginForm()
        context = {'form': form}
        return render_to_response('login.html', context, context_instance=RequestContext(request))

"""
Handle logout requests
"""
def LogoutRequest(request):
    if 'is_patient' in request.session:
        del request.session['is_patient']
    newLog = ActivityLog(user=request.user.username,time_logged=datetime.now(),message=request.user.username + ' logged out.')
    newLog.save()
    logout(request)
    return HttpResponseRedirect('/')

"""
Handle navigation to the profile page after successful login
"""
@login_required
def Profile(request):
    # If user is not logged in, send them to login page
    if not request.user.is_authenticated():
        return HttpResponseRedirect('/login/')

    user = request.user

    # Django is weird and won't let hasattr() work without throwing an exception.
    # These try/except blocks will catch all types of valid logins
    try:
        patient = user.patient
        context = {'patient': patient}
        request.session['is_patient'] = True
        return render_to_response('patient_panel.html', context, context_instance=RequestContext(request))
    except ObjectDoesNotExist:
        try:
            doctor = user.doctor
            presc_form = PrescriptionForm()
            context = {'doctor': doctor, 'presc_form': presc_form}
            return render_to_response("doctor_panel.html", context, context_instance=RequestContext(request))
        except ObjectDoesNotExist:
            try:
                nurse = user.nurse
                context = {'nurse': nurse}
                return render_to_response("nurse_panel.html", context, context_instance=RequestContext(request))
            except ObjectDoesNotExist:
                try:
                    administrator = user.administrator
                    form = AdminForm()
                    search = SearchForm()
                    context = {'administrator': administrator, 'form': form, 'search': search}
                    return render_to_response("administrator_panel.html", context, context_instance=RequestContext(request))
                except ObjectDoesNotExist:
                    if user.is_superuser:
                        return HttpResponseRedirect('/admin/')
                    else:
                        return HttpResponseRedirect('/')
"""
PatientUpdate view for when a patient is making changes to their profile
information.
"""
@login_required
def PatientUpdate(request):
    user = User.objects.get(pk=request.user.id)
    if request.method == 'POST':
        form = ProfileForm(request.POST, instance=user.patient)
        if form.is_valid():
            form.save()
            # Log updated activity
            newLog = ActivityLog(user=user.username,time_logged=datetime.now(),message=user.username + ' has updated his/her profile.')
            newLog.save()
            return HttpResponseRedirect('/profile/')
    else:
        form = ProfileForm(instance=user.patient)

    return render_to_response('update_profile.html',
        locals(), context_instance=RequestContext(request))

def DoctorUpdate(request):
    try:
        doctor = request.user.doctor
    except ObjectDoesNotExist:
        return HttpResponseRedirect('/')

    if request.method == 'POST':
        form = DoctorProfileForm(request.POST, instance=doctor)
        if form.is_valid():
            form.save()
            newLog = ActivityLog(user=request.user.username,time_logged=datetime.now(),message=request.user.username + ' has updated his/her profile.')
            newLog.save()
            return HttpResponseRedirect('/profile/')
    else:
        form = DoctorProfileForm(instance=doctor)

    return render_to_response('doctor_update.html', locals(), context_instance=RequestContext(request))


"""
AddUser view handles admin's adding of users to the system.
"""
@login_required
def AddUser(request):

    if request.method == 'POST':
        form = AdminForm(request.POST)
        # Pass form back through all the clean methods we had created and make new user
        if form.is_valid():
            user = User.objects.create_user(username=form.cleaned_data['username'],
                                                                       email = form.cleaned_data['email'],
                                                                       password=form.cleaned_data['password'])
            user.save()

            if form.cleaned_data['create_choices'] == 'AD':
                administrator = Administrator(user=user,
                first_name=form.cleaned_data['first_name'],
                mid_initial=form.cleaned_data['mid_initial'],
                last_name=form.cleaned_data['last_name'],
                phone=form.cleaned_data['phone'])
                administrator.save()
            elif form.cleaned_data['create_choices'] == 'RN':
                nurse = Nurse(user=user,
                first_name=form.cleaned_data['first_name'],
                mid_initial=form.cleaned_data['mid_initial'],
                last_name=form.cleaned_data['last_name'],
                phone=form.cleaned_data['phone'])
                nurse.save()
            else:
                doctor = Doctor(user=user,
                first_name=form.cleaned_data['first_name'],
                mid_initial=form.cleaned_data['mid_initial'],
                last_name=form.cleaned_data['last_name'],
                phone=form.cleaned_data['phone'])
                doctor.save()
            # Log this registration activity.
            newLog = ActivityLog(user=user.username,time_logged=datetime.now(),message=user.username + ' has been added to system.')
            newLog.save()
            # registration was a success; go back to the panel
            return HttpResponseRedirect('/profile/')

        else:
            # Send them back to their profile page if the form isn't valid.
            administrator = request.user.administrator
            form = AdminForm(request.POST)
            search = SearchForm()
            context = {'administrator': administrator, 'form': form, 'search': search}
            return render_to_response("administrator_panel.html", context, context_instance=RequestContext(request))

    else:
        # You can't access this with normal GET requests. Send them back to their panel.
        return HttpResponseRedirect('/profile/')
"""
View that handles the search feature in various user perspectives.
"""
def SearchUser(request):

    if request.method == 'POST':

        search = SearchForm(request.POST)

        if search.is_valid():

            first_name = search.cleaned_data['first_name']
            last_name = search.cleaned_data['last_name']
            type = search.cleaned_data['type']
            results = []

            if type == 'DR':
                request.session['type'] = 'doctor'
                results += list(Doctor.objects.filter(first_name = first_name, last_name = last_name))
            elif type == 'PT':
                request.session['type'] = 'patient'
                results += list(Patient.objects.filter(first_name = first_name, last_name = last_name))
            elif type == 'RN':
                request.session['type'] = 'nurse'
                results += list(Nurse.objects.filter(first_name = first_name, last_name = last_name))
            elif type == 'AD':
                request.session['type'] = 'administrator'
                results += list(Administrator.objects.filter(first_name = first_name, last_name = last_name))

            administrator = request.user.administrator
            form = AdminForm()
            deleteform = QueryForm()
            context = {'administrator': administrator, 'form': form, 'search': search, 'results': results, 'deleteform': deleteform}
            return render_to_response("administrator_panel.html", context, context_instance=RequestContext(request))
        else:
            return HttpResponseRedirect('/profile/')
    else:
        return HttpResponseRedirect('/profile/')

def DeleteUser(request):

    if request.method == 'POST':
        deleteform = QueryForm(request.POST)
        if deleteform.is_valid():
            id = deleteform.cleaned_data['id']
            if request.session['type'] == 'doctor':
                doc = Doctor.objects.get(pk = id)
                doc.user.is_active = False
                # clear all relations to patients
                doc.patient_set.clear()
                # remove all related prescriptions and appointments
                Appointment.objects.filter(doctor = doc).delete()
                Prescription.objects.filter(doctor = doc).delete()
                doc.user.save()
            elif request.session['type'] == 'patient':
                pat = Patient.objects.get(pk = id)
                pat.user.is_active = False
                # clear this patient from the doctor's list
                pat.doctor.patient_set.remove(pat)
                # remove all related prescriptions and appointments
                Appointment.objects.filter(patient = pat).delete()
                Prescription.objects.filter(patient = pat).delete()
                pat.user.save()
            elif request.session['type'] == 'nurse':
                nur = Nurse.objects.get(pk = id)
                nur.user.is_active = False
                nur.user.save()
            elif request.session['type'] == 'administrator':
                adm = Administrator.objects.get(pk = id)
                adm.user.is_active = False
                adm.user.save()

            del request.session['type']
            return HttpResponseRedirect('/profile/')

        else:
            return HttpResponseRedirect('/profile/')

    else:
        return HttpResponseRedirect('/profile/')

"""
This view handles appointment creation for a given
user
"""
def CreateAppointment(request):

    type = 'none'

    if request.method == 'POST':

        try:
            doctor = request.user.doctor
            form = AppointmentFormDoctor(request.POST)
            type = 'doctor'
            appointments = doctor.getAppointments()
        except ObjectDoesNotExist:
            try:
                patient = request.user.patient
                form = AppointmentFormPatient(request.POST)
                type = 'patient'
                appointments = patient.getAppointments()
            except ObjectDoesNotExist:
                nurse = request.user.nurse
                form = AppointmentFormNurse(request.POST)
                appointments = Appointment.objects.all()
                type = 'nurse'

        if form.is_valid():
            start_date = form.cleaned_data['start_date']
            start_time = form.cleaned_data['start_time']
            end_time = form.cleaned_data['end_time']

            if type == 'doctor':
                patient = form.cleaned_data['patient']
                new_app = Appointment(start_date = start_date, start_time = start_time, end_time = end_time, doctor = request.user.doctor, patient = patient)
                new_app.save()
            elif type == 'patient':
                doctor = form.cleaned_data['doctor']
                new_app = Appointment(start_date = start_date, start_time = start_time, end_time = end_time, doctor = doctor, patient = request.user.patient)
                new_app.save()
            elif type == 'nurse':
                doctor = form.cleaned_data['doctor']
                patient = form.cleaned_data['patient']
                new_app = Appointment(start_date = start_date, start_time = start_time, end_time = end_time, doctor = doctor, patient = patient)
                new_app.save()
            # Log this activity.
            newLog = ActivityLog(user=request.user.username,time_logged=datetime.now(),message=request.user.username + ' has made a new appointment.')
            newLog.save()
            return HttpResponseRedirect('/profile/')
        else:
            # Push the error messages to the current page
            return render_to_response('appointment.html', {'form': form, 'appointments': appointments}, context_instance=RequestContext(request))

    elif request.method == "DELETE":
        Appointment.objects.get(pk=request.DELETE['id']).delete()

    else:
        # get request; generate form based on user type
        try:
            doctor = request.user.doctor
            appointments = doctor.getAppointments()
            form = AppointmentFormDoctor()
            type = 'doctor'
        except ObjectDoesNotExist:
            try:
                patient = request.user.patient
                appointments = patient.getAppointments()
                form = AppointmentFormPatient()
                type = 'patient'
            except ObjectDoesNotExist:
                nurse = request.user.nurse
                form = AppointmentFormNurse()
                appointments = nurse.getAppointments()
                queryform = QueryForm()
                context = {'form': form, 'appointments': appointments, 'queryform': queryform}
                return render_to_response("appointment.html", context, context_instance=RequestContext(request))


        queryform = QueryForm()
        context = {'form': form, 'appointments': appointments, 'queryform': queryform}
        return render_to_response("appointment.html", context, context_instance=RequestContext(request))

"""
View that handles the searching for appointments feature
"""
def AppointmentQuery(request):
    if request.method == 'POST':
        form = QueryForm(request.POST)
        if form.is_valid():
            id = form.cleaned_data['id']
            request.session['app_id'] = id
            app = Appointment.objects.get(id = id)
            modifyForm = AppointmentFormNurse(instance=app)
            # Determine whether to render the patient field for the modify form!
            if 'is_patient' in request.session:
                should_render = False
            else:
                should_render = True
            context = {'form': modifyForm, 'should_render': should_render}
            return render_to_response("appointment_view.html", context, context_instance=RequestContext(request))
        else:
            return render_to_response('appointment.html', {'form': form}, context_instance=RequestContext(request))
    else:
        return HttpResponseRedirect("/profile/")

"""
Handles modification of appointments by valid users
"""
def AppointmentModify(request):

    if request.method == 'POST':
        # check if the session is a patient; change the form accordingly
        if 'is_patient' in request.session:
            should_render = False
            form = AppointmentFormPatient(request.POST)
            if form.is_valid():
                id = request.session['app_id']
                old_app = Appointment.objects.get(pk = id).delete()
                start_date=form.cleaned_data['start_date']
                start_time=form.cleaned_data['start_time']
                end_time=form.cleaned_data['end_time']
                doctor=form.cleaned_data['doctor']
                new_app = Appointment(pk = id, start_date = start_date, start_time = start_time, end_time = end_time, doctor = doctor, patient = request.user.patient)
                new_app.save()
                request.session.pop('app_id')
                # Log this activity.
                newLog = ActivityLog(user=request.user.username,time_logged=datetime.now(),message=request.user.username + ' has modified an appointment.')
                newLog.save()
                return HttpResponseRedirect('/profile/')
            else:
                return render_to_response('appointment_view.html', {'form': form, 'should_render': should_render}, context_instance=RequestContext(request))
        else:
            should_render = True

        form = AppointmentFormNurse(request.POST)
        if form.is_valid():
            id = request.session['app_id']
            old_app = Appointment.objects.get(pk = id).delete()
            start_date=form.cleaned_data['start_date']
            start_time=form.cleaned_data['start_time']
            end_time=form.cleaned_data['end_time']
            doctor=form.cleaned_data['doctor']
            patient=form.cleaned_data['patient']
            new_app = Appointment(pk = id, start_date = start_date, start_time = start_time, end_time = end_time, doctor = doctor, patient = patient)
            new_app.save()
            request.session.pop('app_id')
            # Log this activity.
            newLog = ActivityLog(user=request.user.username,time_logged=datetime.now(),message=request.user.username + ' has modified an appointment.')
            newLog.save()
            return HttpResponseRedirect('/profile/')
        else:
            return render_to_response('appointment_view.html', {'form': form, 'should_render': should_render}, context_instance=RequestContext(request))
    else:
        return HttpResponseRedirect('/profile/')


"""
PrescriptionsPanel presents different fields when creating a prescription
"""
def PrescriptionsPanel(request):
    try:
        doctor = request.user.doctor
    except Exception:
        return HttpResponseRedirect("/")

    form = PrescriptionForm()
    queryform = QueryForm()
    prescriptions = doctor.getPrescriptions()
    context = {'form': form, 'prescriptions': prescriptions, 'queryform': queryform}
    return render_to_response("prescriptions.html", context, context_instance=RequestContext(request))

"""
View presented to (valid) user when deleting a prescription a patient has
"""
def DeletePrescription(request):

    if request.method == 'POST':

        form = QueryForm(request.POST)

        if form.is_valid():
            id = form.cleaned_data['id']
            try:
                # grab the patient's name then delete the prescriptions
                prescription = Prescription.objects.get(pk=id)
                patient = prescription.patient
                prescription.delete()
                newLog = ActivityLog(user=request.user.username,time_logged=datetime.now(),message=request.user.username + ' has deleted a prescription for ' + patient.first_name)
                newLog.save()
            except Exception:
                pass
           
            return HttpResponseRedirect('/profile/')
        else:
            return HttpResponseRedirect('/profile/')
    else:
        return HttpResponseRedirect('/')
"""
View for adding a prescription to a patient after creation
"""
def AddPrescription(request):

    if request.method == 'POST':

        form = PrescriptionForm(request.POST)

        if form.is_valid():
            start_date = form.cleaned_data['start_date']
            end_date = form.cleaned_data['end_date']
            name = form.cleaned_data['name']
            manufacturer = form.cleaned_data['manufacturer']
            dosage = form.cleaned_data['dosage']
            instructions = form.cleaned_data['instructions']
            patient = form.cleaned_data['patient']
            new_pres = Prescription(start_date = start_date,
                                    end_date = end_date,
                                    name = name,
                                    manufacturer = manufacturer,
                                    dosage = dosage,
                                    instructions = instructions,
                                    patient = patient,
                                    doctor = request.user.doctor )
            new_pres.save()
            # Log this activity.
            newLog = ActivityLog(user=request.user.username,time_logged=datetime.now(),message=request.user.username + ' has added a prescription for ' + patient.first_name)
            newLog.save()
            return HttpResponseRedirect('/profile/')

        else:
            return render_to_response('prescriptions.html', {'form': form}, context_instance=RequestContext(request))

    else:
        return HttpResponseRedirect('/profile/')
