"""
forms.py

@author Jason McMullen, Tyler Russell
"""

from django import forms
from django.contrib.auth.models import User
from django.forms import ModelForm
from health.models import Patient, Doctor, CommonInfo, Appointment, Prescription
from localflavor.us.forms import USPhoneNumberField, USStateField, USZipCodeField
from datetime import datetime, timedelta, time, date
from django.core.exceptions import ObjectDoesNotExist
from django.core.validators import RegexValidator

"""
Information needed to be gathered to create a user object that will then be
associated with a patient object
"""
class RegistrationForm(ModelForm):

    # Custom error messages for dates
    def __init__(self, *args, **kwargs):
        super(RegistrationForm, self).__init__(*args, **kwargs)

        self.fields['dob'].error_messages = {'required': 'The date must be in the format of: MM/DD/YYYY',
                                                    'invalid': 'The date must be in the format of: MM/DD/YYYY'}

    username = forms.CharField(label='User Name')
    email = forms.EmailField(label='Email Address')
    password = forms.CharField(label='Password', widget=forms.PasswordInput(render_value=False))
    password1 = forms.CharField(label='Confirm Password', widget=forms.PasswordInput(render_value=False))

    # Import all the information from Patient form into this one, do not need username again so exclude it
    class Meta:
        model = Patient
        exclude = ('user',)

    # This method checks to see if the username is available for use
    def clean_username(self):
        username = self.cleaned_data['username']
        try:
            User.objects.get(username=username)
        except User.DoesNotExist:
            return username
        raise forms.ValidationError("That user name already exists. Please select another.")

    # This method checks to see if the passwords equal each other
    def clean_password1(self):
        if self.cleaned_data.get('password') != self.cleaned_data.get('password1'):
             raise forms.ValidationError("The passwords did not match. Please try again.")
        return self.cleaned_data

    # Check for the values of dob
    def clean_dob(self):

        dob = self.cleaned_data['dob']

        # Cannot be born on a date prior to today (Accepting today just cause babies)
        if dob > datetime.now().date():
           raise forms.ValidationError("A date of birth cannont take place in the future.")

        return dob


"""
Information needed to be gathered for LoginRequest
"""
class LoginForm(forms.Form):
    username = forms.CharField(label='User Name')
    password = forms.CharField(label='Password', widget=forms.PasswordInput(render_value=False))

"""
Patient update profile format
"""
class ProfileForm(ModelForm):

    class Meta:
        model = Patient
        exclude = ('user',)
"""
Doctor update profile format
"""
class DoctorProfileForm(ModelForm):

    class Meta:
        model = Doctor
        exclude = ('user',)

"""
Form that gets information for creating different types of administrators.
"""
class AdminForm(forms.Form):
    USER_TYPES = (
        ('AD', 'Administrator'),
        ('RN', 'Nurse'),
        ('DR', 'Doctor')
    )

    create_choices = forms.ChoiceField(choices=USER_TYPES, label='User Type')

    # Validator to control what the user is entering into the fields
    alpha = RegexValidator(r'^[a-zA-Z ]*$', 'Only alpha characters are allowed.')


    # Cannot "inherit" from abstract models (aka CommonInfo) so had to repeat myself.
    first_name  = forms.CharField(label="First Name", validators=[alpha])
    mid_initial = forms.CharField(max_length=1,label="Middle Initial", required=False, validators=[alpha])
    last_name   = forms.CharField(label="Last Name", validators=[alpha])
    phone = USPhoneNumberField()

    username = forms.CharField(label='User Name')
    email = forms.EmailField(label='Email Address')
    password = forms.CharField(label='Password', widget=forms.PasswordInput(render_value=False))
    password1 = forms.CharField(label='Confirm Password', widget=forms.PasswordInput(render_value=False))

    # This method checks to see if the username is available for use
    def clean_username(self):
        username = self.cleaned_data['username']
        try:
            User.objects.get(username=username)
        except User.DoesNotExist:
            return username
        raise forms.ValidationError("That user name already exists. Please select another.")

    # This method checks to see if the passwords equal each other
    def clean_password1(self):
        if self.cleaned_data.get('password') != self.cleaned_data.get('password1'):
             raise forms.ValidationError("The passwords did not match. Please try again.")
        return self.cleaned_data

"""
Form used for the search feature from different users' points of view
"""
class SearchForm(forms.Form):
    USER_TYPES = (
        ('AD', 'Administrator'),
        ('RN', 'Nurse'),
        ('DR', 'Doctor'),
        ('PT', 'Patient')
    )

    type = forms.ChoiceField(choices=USER_TYPES, label='User Type')
    first_name = forms.CharField(label="First Name")
    last_name = forms.CharField(label="Last Name")

"""
Form used for when doctors create appointments.
"""
class AppointmentFormDoctor(forms.ModelForm):
    class Meta:
        model = Appointment
        exclude = ('doctor',)

    def __init__(self, *args, **kwargs):
        super(AppointmentFormDoctor, self).__init__(*args, **kwargs)

        self.fields['start_date'].error_messages = {'required': 'The date must be in the format of: MM/DD/YYYY',
                                                    'invalid': 'The date must be in the format of: MM/DD/YYYY'}
        self.fields['start_time'].error_messages = {'required': 'The time must be in the format of: HH:MM',
                                                    'invalid': 'The time must be in the format of: HH:MM'}
        self.fields['end_time'].error_messages = {'required': 'The time must be in the format of: HH:MM',
                                                    'invalid': 'The time must be in the format of: HH:MM'}

    # This method will check to see if the user entered a correct date
    def clean_start_date(self):

        start_date = self.cleaned_data['start_date']

        # Cannot have appointments in the past
        if start_date < date.today():
            raise forms.ValidationError("Cannot create appointments in the past")

        return start_date

    # This method checks to see if the user has entered an appointment that will last for at least 10 minutes
    def clean_end_time(self):

        # The end time of the appointment
        end_time = self.cleaned_data['end_time']

        # The start time of the appointment
        startTime = self.cleaned_data.get('start_time')

        # The start date
        startDate = self.cleaned_data.get('start_date')

        # Check to make sure the date entered is not null
        if startDate is None:
            return end_time

        # Check to make sure the starting time entered is not null
        if startTime is None:
            return end_time

        # Need to combine the times and date of appointment in order to do any operations on it
        startDate_and_startTime = datetime.combine(startDate, startTime)
        startDate_and_endTime = datetime.combine(startDate, end_time)

        # The amount of time we want to the appointment to last
        delta = timedelta(minutes = 10)

        # If the start time with 10 more minutes is greater than the entered end time, then the user to allocate
        # enough time for the appointment
        if (startDate_and_startTime + delta) > startDate_and_endTime:
             raise forms.ValidationError("The appointment needs to be at least 10 minutes long.")

        return end_time

"""
Form used for when a Patient manages an appointment
"""
class AppointmentFormPatient(forms.ModelForm):
    class Meta:
        model = Appointment
        exclude = ('patient',)


    def __init__(self, *args, **kwargs):
        super(AppointmentFormPatient, self).__init__(*args, **kwargs)

        self.fields['start_date'].error_messages = {'required': 'The date must be in the format of: MM/DD/YYYY',
                                                    'invalid': 'The date must be in the format of: MM/DD/YYYY'}
        self.fields['start_time'].error_messages = {'required': 'The time must be in the format of: HH:MM',
                                                    'invalid': 'The time must be in the format of: HH:MM'}
        self.fields['end_time'].error_messages = {'required': 'The time must be in the format of: HH:MM',
                                                    'invalid': 'The time must be in the format of: HH:MM'}

    # This method will check to see if the user entered a correct date
    def clean_start_date(self):

        start_date = self.cleaned_data['start_date']

        # Cannot have appointments in the past
        if start_date < date.today():
            raise forms.ValidationError("Cannot create appointments in the past")

        return start_date

    # This method checks to see if the user has entered an appointment that will last for at least 10 minutes
    def clean_end_time(self):

        # The end time of the appointment
        end_time = self.cleaned_data['end_time']

        # The start time of the appointment
        startTime = self.cleaned_data.get('start_time')

        # The start date
        startDate = self.cleaned_data.get('start_date')

        # Check to make sure the date entered is not null
        if startDate is None:
            return end_time

        # Check to make sure the starting time entered is not null
        if startTime is None:
            return end_time

        # Need to combine the times and date of appointment in order to do any operations on it
        startDate_and_startTime = datetime.combine(startDate, startTime)
        startDate_and_endTime = datetime.combine(startDate, end_time)

        # The amount of time we want to the appointment to last
        delta = timedelta(minutes = 10)

        print(startDate_and_startTime + delta)

        # If the start time with 10 more minutes is greater than the entered end time, then the user to allocate
        # enough time for the appointment
        if (startDate_and_startTime + delta) > startDate_and_endTime:
            raise forms.ValidationError("The appointment needs to be at least 10 minutes long.")

        return end_time

"""
Form used for when nurses manage an appointment.
"""
class AppointmentFormNurse(forms.ModelForm):
    class Meta:
        model = Appointment

    # Creating custom error messages for the fields on this form
    def __init__(self, *args, **kwargs):
        super(AppointmentFormNurse, self).__init__(*args, **kwargs)

        self.fields['start_date'].error_messages = {'required': 'The date must be in the format of: MM/DD/YYYY',
                                                    'invalid': 'The date must be in the format of: MM/DD/YYYY'}
        self.fields['start_time'].error_messages = {'required': 'The time must be in the format of: HH:MM',
                                                    'invalid': 'The time must be in the format of: HH:MM'}
        self.fields['end_time'].error_messages = {'required': 'The time must be in the format of: HH:MM',
                                                    'invalid': 'The time must be in the format of: HH:MM'}

    # This method checks to see if the user has entered an appointment that will last for at least 10 minutes
    def clean_end_time(self):

        # The end time of the appointment
        end_time = self.cleaned_data['end_time']

        # The start time of the appointment
        startTime = self.cleaned_data.get('start_time')

        # The start date
        startDate = self.cleaned_data.get('start_date')

        # Check to make sure the date entered is not null
        if startDate is None:
            return end_time

        # Check to make sure the starting time entered is not null
        if startTime is None:
            return end_time

        # Need to combine the times and date of appointment in order to do any operations on it
        startDate_and_startTime = datetime.combine(startDate, startTime)
        startDate_and_endTime = datetime.combine(startDate, end_time)

        # The amount of time we want to the appointment to last
        delta = timedelta(minutes = 10)

        # If the start time with 10 more minutes is greater than the entered end time, then the user to allocate
        # enough time for the appointment
        if (startDate_and_startTime + delta) > startDate_and_endTime:
             raise forms.ValidationError("The appointment needs to be at least 10 minutes long.")

        return end_time

"""
Form used for when a user searches for a particular appointment.
"""
class QueryForm(forms.Form):
    id = forms.IntegerField(min_value = 1)

"""
Form for creating a prescription by a doctor.
"""
class PrescriptionForm(forms.ModelForm):

    # This method will check to see if the user entered a correct date
    def clean_start_date(self):

        start_date = self.cleaned_data['start_date']

        # Cannot have appointments in the past
        if start_date < date.today():
            raise forms.ValidationError("A precription cannot not be prescribed on a past date")

        return start_date

    # This method will check to see if the user entered a correct date
    def clean_end_date(self):

        end_date = self.cleaned_data['end_date']
        start_date = self.cleaned_data.get('start_date')

        # Check if start_date is not null
        if start_date is None:
            return end_date

        # Cannot have appointments in the past
        if end_date < date.today():
            raise forms.ValidationError("The prescription cannot end on a past date")

        # Check if end_date is before start_date
        if end_date <= start_date:
            raise forms.ValidationError("The end date of a prescription cannot be on or before the start date")

        return end_date

    class Meta:
        model = Prescription
        exclude = ('doctor',)

    # Custom error messages for dates
    def __init__(self, *args, **kwargs):
        super(PrescriptionForm, self).__init__(*args, **kwargs)

        self.fields['start_date'].error_messages = {'required': 'The date must be in the format of: MM/DD/YYYY',
                                                    'invalid': 'The date must be in the format of: MM/DD/YYYY'}
        self.fields['end_date'].error_messages = {'required': 'The date must be in the format of: MM/DD/YYYY',
                                                    'invalid': 'The date must be in the format of: MM/DD/YYYY'}
