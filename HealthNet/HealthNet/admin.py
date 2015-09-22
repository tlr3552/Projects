"""
admin.py

@author Jason McMullen
"""
from django.contrib.admin.sites import AdminSite
from health.models import Patient

class HealthNetAdmin(AdminSite):
    pass

health_admin=HealthNetAdmin()

