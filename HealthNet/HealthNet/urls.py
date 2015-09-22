"""
urls.py

desc: defines URL patterns and assigns functions to them.

@author Jason McMullen
"""

from django.conf.urls import *
from django.conf import settings
from django.conf.urls.static import static
from django.contrib import admin

admin.autodiscover()

urlpatterns = patterns('',
    url(r'^$', 'HealthNet.views.home', name='home'),
    url(r'^admin/', include(admin.site.urls)),
    url(r'^register/$', 'health.views.PatientRegistration', name="register"),
    url(r'^profile/$', 'health.views.Profile', name="profile"),
    url(r'^login/$', 'health.views.LoginRequest', name="login"),
    url(r'^logout/$', 'health.views.LogoutRequest', name="logout"),
    url(r'^update_profile/', 'health.views.PatientUpdate', name="update_profile"),
    url(r'^doctor_update/', 'health.views.DoctorUpdate', name="doctor_update"),
    url(r'^profile/adduser/$', 'health.views.AddUser', name="add_user"),
    url(r'^profile/searchuser/$', 'health.views.SearchUser', name="search_user"),
    url(r'^profile/deleteuser/$', 'health.views.DeleteUser', name="delete_user"),
    url(r'^profile/appointment/$','health.views.CreateAppointment',name="appointment"),
    url(r'^profile/appointment/modify/$', 'health.views.AppointmentQuery', name="appointment_query"),
    url(r'^profile/appointment/modify/save/$', 'health.views.AppointmentModify', name="appointment_modify"),
    url(r'^profile/prescriptions/$', 'health.views.PrescriptionsPanel', name='prescriptions_panel'),
    url(r'^profile/prescriptions/add/$', 'health.views.AddPrescription', name="prescriptions_add"),
    url(r'^profile/prescriptions/delete/$', 'health.views.DeletePrescription', name="prescriptions_delete")
)

if settings.DEBUG:
    urlpatterns += static(settings.STATIC_URL,
                          document_root=settings.STATIC_ROOT)
    urlpatterns += static(settings.MEDIA_URL,
                          document_root=settings.MEDIA_ROOT)
