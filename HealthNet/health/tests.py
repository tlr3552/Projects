"""
test.py

run cmd = python manage.py test

Note: all test methods must start with "test_" followed by rest of the name. If of a different format, django will
not run/see the test

@author Jason McMullen
"""

from django.test import TestCase, Client
from django.core.urlresolvers import reverse
from health.models import *
from health.forms import *
from django.contrib.auth.models import User

# Most simpliest test I can think of to make sure the unit tests work when ran.
# This test should always pass no matter what
class Simple(TestCase):
    def test_simple(self):
        self.assertEqual(2+2, 4)


# Test cases to see if all of the pages are accessible
class ViewsTest(TestCase):

    # Create a new user to test login redirect functionality
    def setUp(self):
        self.client = Client()
        self.theUser = User.objects.create_user('Walter', '1@gmail.com', '12345')

    # Test the index page
    def test_index(self):
        response = self.client.get('/')
        self.assertEqual(response.status_code, 200)
        self.assertTemplateUsed(response, 'index.html')

    # Test the admin page (superuser)
    def test_admin(self):
        response = self.client.get('/admin/')
        self.assertEqual(response.status_code, 200)
        # Not using our own admin page

    # Test the register page
    def test_register(self):
        response = self.client.get('/register/')
        self.assertEqual(response.status_code, 200)
        self.assertTemplateUsed(response, 'register.html')

    # Test registering a Patient -- should redirect to profile page after registering
    def test_register_patient(self):
        response = self.client.post('/register/', {'username': 'Sam', 'email': '1@gmail.com', 'password': '12345', 'password1': '12345',
                                                   'first_name': 'Sam', 'mid_initial': 'S', 'last_name': 'Smith', 'phone': '555-555-5555',
                                                   'appt_num': '1', 'address': '123', 'city': 'Rochester', 'state': 'NY', 'zipcode': '12345',
                                                   'dob': '1/11/1111', 'ins_name': 'Something', 'ins_num': '54321',
                                                   'ins_phone': '123-456-7890', 'pref_hospital': 'Cares', 'emerg_first_name': 'Hi',
                                                   'emerg_last_name': 'Bye', 'emerg_phone': '000-000-0000'}, follow = True)
        self.assertEqual(response.status_code, 200)

    # Test the profile page -- not logged in so should send to login page
    def test_profile(self):
        response = self.client.get('/profile/')
        self.assertEqual(response.status_code, 302)
        self.assertRedirects(response, '/login/?next=/profile/')

    # Test the login page
    def test_login(self):
        response = self.client.get('/login/')
        self.assertEqual(response.status_code, 200)
        self.assertTemplateUsed(response, 'login.html')

    # Test if the login page redirects after logging in
    def test_login_redirect(self):
        response = self.client.post('/login/', {'username': 'Walter', 'password': '12345'})
        self.assertEqual(response.status_code, 302)

    # Test if logging out will redirect to index.html (the homepage of healthnet)
    def test_logout_redirect(self):
        response = self.client.post('/logout/')
        self.assertEqual(response.status_code, 302)
        self.assertRedirects(response, '/')

    # Test for update profile -- no user is logged in, should not go to the page
    def test_update_profile(self):
        response = self.client.get('/update_profile/')
        self.assertEqual(response.status_code, 302)


# Tests for the Patient Class
class PatientTest(TestCase):

    # This test will only work if the User object has been commented out in the Patient class.
    # Once factoring has been completed this test will be revisited and altered to run under
    # all coniditions.
    def setUp(self):

        user = User.objects.create_user('Bob', '1@gmail.com', 'bar')
        Patient.objects.create(user = user,
                               first_name = "Bob", mid_initial = "B", last_name = "Smith",
                               appt_num = "1", address = "111 Some", city = "Where", state = "NY",
                               zipcode = "12345", dob = "1111-11-1", phone = "1234567890",
                               ins_name = "Cool", ins_num = "123", ins_phone = "0987654321",
                               emerg_first_name = "Walter", emerg_last_name = "White",
                               emerg_phone = "5555555555")
        client = Client()
        client.theUser = user

    # This test is to test if the patient object is created and the information has been saved.
    # This test checks that the middle initials match and will throw an error if they don't.
    def test_patient(self):
        p = Patient.objects.get(first_name = "Bob")
        self.assertEqual(p.mid_initial, "B")

    # Test to see if logging in will send use to their profile page
    def test_patient_profile(self):
        response = self.client.post('/login/', {'username': 'Bob', 'password': 'bar'})
        self.assertEqual(response.status_code, 302)
        self.assertRedirects(response, '/profile/')


# Tests for the Administrator Class
class AdminTest(TestCase):

    # Create a test administrator object
    def setUp(self):

        user = User.objects.create_user('AdminMan', '3@gmail.com', '12345')
        Administrator.objects.create(user = user, first_name = "AdminMans", mid_initial = "Z", last_name = "Something", 
                                     phone = "000-000-0000")
        client = Client()
        client.theUser = user

    # Test to see if an administrator object was created and saved in db
    def test_admin(self):

        a = Administrator.objects.get(first_name = "AdminMans")
        self.assertEqual(a.mid_initial, "Z")

    # Test to see if logging in will send use to their profile page
    def test_admin_profile(self):
        response = self.client.post('/login/', {'username': 'AdminMan', 'password': '12345'})
        self.assertEqual(response.status_code, 302)
        self.assertRedirects(response, '/profile/')


# Tests for the Doctor Class
class DoctorTest(TestCase):

    # Create a test doctor object
    def setUp(self):

        user = User.objects.create_user('DoctorMan', '2@gmail.com', '12345')
        Doctor.objects.create(user = user, first_name = "DoctorMans", mid_initial = "D", last_name = "Ok",
                              phone = "012-345-6789")
        client = Client()
        client.theUser = user

    # Test to see if a doctor object was created and saved in db
    def test_doctor(self):

        d = Doctor.objects.get(first_name = "DoctorMans")
        self.assertEqual(d.mid_initial, "D")

    # Test to see if logging in will send use to their profile page
    def test_patient_profile(self):
        response = self.client.post('/login/', {'username': 'DoctorMan', 'password': '12345'})
        self.assertEqual(response.status_code, 302)
        self.assertRedirects(response, '/profile/')


# Tests for the Nurse Class
class NurseTest(TestCase):

    # Create a test Nurse object
    def setUp(self):

        user = User.objects.create_user('NurseMan', '4@gmail.com', '12345')
        Nurse.objects.create(user = user, first_name = "NurseMans", mid_initial = "N", last_name = "Cya",
                             phone = "456-123-7890")
        client = Client()
        client.theUser = user

    # Test to see if a nurse object was created and saved in db
    def test_nurse(self):

        n = Nurse.objects.get(first_name = "NurseMans")
        self.assertEqual(n.mid_initial, "N")

    # Test to see if logging in will send use to their profile page
    def test_patient_profile(self):
        response = self.client.post('/login/', {'username': 'NurseMan', 'password': '12345'})
        self.assertEqual(response.status_code, 302)
        self.assertRedirects(response, '/profile/')
