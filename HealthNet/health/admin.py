from django.contrib import admin
from health.models import Patient
from health.models import ActivityLog
from health.models import Doctor
from health.models import Administrator
from health.models import Nurse
from health.models import Appointment
from health.models import Prescription

admin.site.register(Patient)
admin.site.register(ActivityLog)
admin.site.register(Doctor)
admin.site.register(Administrator)
admin.site.register(Nurse)
admin.site.register(Appointment)
admin.site.register(Prescription)