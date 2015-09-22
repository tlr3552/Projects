"""
views.py
desc: Renders the home page for HealthNet.

@author Amit Mondal
"""

from django.shortcuts import render_to_response

def home(request):
    return render_to_response("index.html")
