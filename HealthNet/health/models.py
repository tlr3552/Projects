"""
models.py

@author Jason McMullen, Amit Mondal, Tyler Russell, Long Pham
"""

from django.db import models
from django.db.models.signals import post_save
from django.contrib.auth.models import User
from django.core.validators import RegexValidator
from localflavor.us.models import PhoneNumberField, USStateField, USZipCodeField
#from datetime import datetime, timedelta, date
#from django.utils import timezone
import datetime
"""
NOTE:
null=True : This field can be None/Null, for the DB
blank=True : This field can be blank, for form validation
"""

"""
Model for common information to all users.
"""
class CommonInfo(models.Model):

    user = models.OneToOneField(User)

    # Validator to control what the user is entering into the fields
    alpha = RegexValidator(r'^[a-zA-Z]*$', 'Only alpha characters without spaces are allowed.')

    first_name  = models.CharField(max_length=100, null=False, blank=False, validators=[alpha])
    mid_initial = models.CharField(max_length=1, null=True, blank=True, validators=[alpha])
    last_name   = models.CharField(max_length=100, null=False, blank=False, validators=[alpha])
    phone = PhoneNumberField()

    class Meta:
        abstract = True

"""
Represents attributes typical to an Appointment. Overrides
save() method in order to prevent creating new appointments
that overlap other appointments.
"""
class Appointment(models.Model):

    start_date = models.DateField()
    start_time = models.TimeField()
    end_time = models.TimeField()
    doctor = models.ForeignKey('Doctor', limit_choices_to={'is_accepting': True})
    patient = models.ForeignKey('Patient')

    # overriding the default save function in order to find overlapping dates/times.
    def save(self, *args, **kwargs):

        # find overlapping dates from the same doctor as this appointment
        overlapping_date_appointments = Appointment.objects.filter(start_date = self.start_date).exclude(id = self.id)

        # out of the appointments that share the same date,
        # find out if saving this appointment will cause a conflict in time
        if (overlapping_date_appointments is not None):

            for appointment in overlapping_date_appointments:

                if ((self.start_time <= appointment.end_time) and (self.end_time >= appointment.start_time)):

                    # there's an overlap, don't save this appointment
                    return

        super(Appointment, self).save(*args, **kwargs)

    def __str__(self):

        # Removing seconds from displayed time, cause who schedules by seconds?
        formatT = "%H:%M"

        # "American" date format
        formatD = "%a %b %d %Y"

        # datetime does other cool things too
        # http://pymotw.com/2/datetime/

        return str(self.id) + " : " + self.doctor.first_name + " with " + self.patient.first_name + " at " + self.start_date.strftime(formatD) + " at " + self.start_time.strftime(formatT) + " - " + self.end_time.strftime(formatT)

"""
Doctor model that extends the CommonInfo model. Defines methods
to get related models to a Doctor instance.
"""
class Doctor(CommonInfo):

    is_accepting = models.BooleanField(default=True)

    def getAppointments(self):
        all_appointments = Appointment.objects.filter(doctor=self)
        return all_appointments

    def getPrescriptions(self):
        all_prescriptions = Prescription.objects.filter(doctor=self)
        return all_prescriptions

    def getPatients(self):
        all_patients = Patient.objects.filter(doctor=self)
        return all_patients

    def __str__(self):
        return str(self.id) + " : " + self.first_name + " " + self.last_name

"""
Model description of a Prescription. Tracks prescription related data, including
the prescribing doctor and the patient its prescribed to, and whether the prescription
is out of date or not.
"""
class Prescription(models.Model):

    # Validators to control what the user is entering into the fields
    alpha = RegexValidator(r'^[a-zA-Z ]*$', 'Only alpha characters are allowed.')
    numeric = RegexValidator(r'^[0-9]*$', 'Only numeric characters are allowed.')
    alphanumeric = RegexValidator(r'^[0-9a-zA-Z ]*$', 'Only alphanumeric characters are allowed.')

    start_date = models.DateField()
    end_date = models.DateField()
    name = models.CharField(max_length=100, validators=[alpha])
    manufacturer = models.CharField(max_length=50, validators=[alpha])
    dosage = models.PositiveIntegerField(max_length=4)
    instructions = models.CharField(max_length=200)

    doctor = models.ForeignKey('Doctor')
    patient = models.ForeignKey('Patient')

    #If the current time is less than the end date, then the prescription is active
    def is_active(self):
        now = datetime.datetime.today()
        return now > self.end_date

        # overriding the default save function in order to find overlapping dates/times
    def save(self, *args, **kwargs):

        # check whether the patient has registered with the doctor
        if (self.patient not in self.doctor.getPatients()):
            print("Patient not registered with you.")
            return
        print(self.start_date, self.end_date)
        super(Prescription, self).save(*args, **kwargs)

    def __str__(self):

        formatD = "%a %b %d %Y"

        return str(self.id) + " : " + self.doctor.first_name + " prescribes for " + self.patient.first_name + " to take " + self.name + " from " + self.start_date.strftime(formatD) + " to " + self.end_date.strftime(formatD)
"""
Patient model, containing common information along with insurance and emergency
contact information, along with their doctor and their prescriptions/appointments.
"""
class Patient(CommonInfo):

    # Validators to control what the user is entering into the fields
    alpha = RegexValidator(r'^[a-zA-Z ]*$', 'Only alpha characters are allowed.')
    numeric = RegexValidator(r'^[0-9]*$', 'Only numeric characters are allowed.')
    alphanumeric = RegexValidator(r'^[0-9a-zA-Z ]*$', 'Only alphanumeric characters are allowed.')

    # Record address
    appt_num = models.CharField(max_length=10, null=True, blank=True, validators=[numeric])
    address  = models.CharField(max_length=120, null=False, blank=False, validators=[alphanumeric])
    city     = models.CharField(max_length=120, null=False, blank=False, validators=[alpha])
    state    = USStateField()
    zipcode  = USZipCodeField()

    # Record DOB (date of birth)
    dob = models.DateField(auto_now_add=False, auto_now=False, null=False, blank=False)

    # Record insurance information
    ins_name  = models.CharField(max_length=100, null=False, blank=False, validators=[alpha])
    ins_num   = models.CharField(max_length=100, null=False, blank=False, validators=[numeric])
    ins_phone = PhoneNumberField()

    # Record preferred hospital
    pref_hospital = models.CharField(max_length=100, null=True, blank=True, validators=[alpha])

    emerg_first_name = models.CharField(max_length=100, null=True, blank=True, validators=[alpha])
    emerg_last_name  = models.CharField(max_length=100, null=True, blank=True, validators=[alpha])
    emerg_phone      = PhoneNumberField(null=True, blank=True)

    # Patient's associated doctor: not required and is initially blank
    doctor = models.ForeignKey(Doctor, null=True, blank=True, default=None, limit_choices_to={'is_accepting': True})

    def getAppointments(self):
        all_appointments = Appointment.objects.filter(patient=self)
        return all_appointments

    def getPrescriptions(self):
        all_prescriptions = Prescription.objects.filter(patient=self)
        return all_prescriptions

    def __str__(self):
        return str(self.id) + " : " + self.first_name + " " + self.mid_initial + " " + self.last_name

"""
Administrator model that tracks system information in a logging
mechanism.
"""
class Administrator(CommonInfo):

    def getLog(self):
        activities = ActivityLog.objects.all().order_by('-time_logged')
        log = []
        for activity in activities:
            log.append(activity)
        return log

    def __str__(self):
        return str(self.id) + " : " + self.first_name + " " + self.mid_initial + " " + self.last_name
"""
Nurse model, can get a list of prescriptions to view,
and manage appointments.
"""
class Nurse(CommonInfo):
    def getAppointments(self):
        all_appointments = Appointment.objects.all()
        return all_appointments

    def getPrescriptions(self):
        all_prescriptions = Prescription.objects.all()
        return all_prescriptions

    def __str__(self):
        return str(self.id) + " : " + self.first_name + " " + self.mid_initial + " " + self.last_name

"""
ActiviyLog model contains information about different events
that occur in the system, viewable by Administrators.
"""
class ActivityLog(models.Model):
    user = models.CharField(max_length=100)
    time_logged = models.DateTimeField()
    message = models.CharField(max_length=100)

    def __str__(self):
        return self.message + " " + self.time_logged.strftime("%A, %d, %B %Y %I:%M%p")
