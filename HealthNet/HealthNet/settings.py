"""
Django settings for HealthNet project.

For more information on this file, see
https://docs.djangoproject.com/en/1.6/topics/settings/

For the full list of settings and their values, see
https://docs.djangoproject.com/en/1.6/ref/settings/
"""

# Build paths inside the project like this: os.path.join(BASE_DIR, ...)
import os
BASE_DIR = os.path.dirname(os.path.dirname(__file__))


# Quick-start development settings - unsuitable for production
# See https://docs.djangoproject.com/en/1.6/howto/deployment/checklist/

# SECURITY WARNING: keep the secret key used in production secret!
SECRET_KEY = '3nqpt9#=)@t72c10ywhnz!*_cmz-a(4gpkero(k-=zn$xf5)iv'

# SECURITY WARNING: don't run with debug turned on in production!
DEBUG = True

TEMPLATE_DEBUG = True

ALLOWED_HOSTS = []

AUTH_USER_MODEL = 'auth.User'

# URL for decorator to use
LOGIN_URL = '/login/'

# Redirect users once they are logged in
LOGIN_REDIRECT_URL='/profile/'

# Application definition

INSTALLED_APPS = (
    'django.contrib.admin',
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.messages',
    'django.contrib.staticfiles',
    'health',
	'localflavor',#For field validation
	# Stop adding apps without adding that package to the repo
    #'south',#For DB migrations
)

MIDDLEWARE_CLASSES = (
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.middleware.common.CommonMiddleware',
    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    'django.middleware.clickjacking.XFrameOptionsMiddleware',
    'health.middleware.HttpPostTunnelingMiddleware',
)

ROOT_URLCONF = 'HealthNet.urls'

WSGI_APPLICATION = 'HealthNet.wsgi.application'

# Database
# https://docs.djangoproject.com/en/1.6/ref/settings/#databases

DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.sqlite3',
        'NAME': os.path.join(BASE_DIR, 'db.sqlite3'),
    }
}

# Internationalization
# https://docs.djangoproject.com/en/1.6/topics/i18n/

LANGUAGE_CODE = 'en-us'

TIME_ZONE = 'UTC'#'America/New_York'

USE_I18N = True

USE_L10N = True

USE_TZ = True


# Static files (CSS, JavaScript, Images)
# https://docs.djangoproject.com/en/1.6/howto/static-files/

STATIC_URL = '/static/'

# Location for templates (so django knows where to get the html for pages)
TEMPLATE_DIRS = (
    os.path.join(os.path.dirname(BASE_DIR), "HealthNet",
                 "static", "templates"),
)

"""
Helpful stuff to have for using bootstrap
"""

if DEBUG:
    MEDIA_URL = '/media/'

    # Where we serve the static files in production
    STATIC_ROOT = os.path.join(os.path.dirname(BASE_DIR), "HealthNet",
                               "static", "static-only")

    # If user wants to upload a picture
    MEDIA_ROOT = os.path.join(os.path.dirname(BASE_DIR), "HealthNet",
                              "static", "media")

    # CSS and javascript files go here
    STATICFILES_DIRS = (
        os.path.join(os.path.dirname(BASE_DIR), "HealthNet",
                     "static", "static"),
    )
