# Java-Instodramat

## Introduction
This is my second Java spring boot project. It is port of my previous Django project "[Django-Instodramat](https://github.com/aFku/Django-Instodramat)" reworked to be a REST API. The app is a clone of the popular social media service Instagram. You can create there a profile with your JWT token, upload images with description, follow other profiles, and like images.  There are many different endpoints, that provide features like showing the latest photos of persons that you follow or showing the list of persons that like a photo. There is also the endpoint that is used to return images instead of JSON responses.

## Stack
- Java 17
- Spring Boot 3.0.2
- MySQL
- Keycloak

## What I've learned
- JWT Authentication and integration with external provider
- How to create integration tests with JUnit
- How to use test containers
- How to create custom security filters
- How to handle files upload and response
- How to add HATEOAS

## Deploy and testing

### How to deploy development setup

You need to go into /Instodrama/deploy and execute command:

<code>docker compose -f deps-compose.yaml up</code>

This command will deploy MySQL database and Keycloak. Keycloak will be provisioned with realm-instodramat.json file which is used to configure whole realm.
After deployment go to keycloak admin panel (by default):

<code>http://localhost:8081/ </code>

and login with creds admin:admin. Add user with password to Instodramat realm.
Now you can go to /Instodramat directory and execute:

<code>mvn spring-boot:run</code>

After that you can obtain JWT from keycloak and use all available endpoints (create profile first).

### How to run tests

To run tests, just go to the /Instodramat directory and execute:

<code>mvn test</code>

## Endpoints

All endpoints started with /api/v1 and /images except /api/v1/profiles/create require profile related to JWT token that you use.
Also, OpenAPI docs endpoints are available for everyone.

### swagger-ui/index.html

OpenAPI docs with swagger-ui.

### [POST] /api/v1/profiles/create

You can create your profile there.

### [GET] /api/v1/profiles

Get list of all created profiles.

### [GET] /api/v1/profiles/{profileId : int}

Get specific profile with given ID.

### [PATCH] /api/v1/profiles/{profileId : int}

Update profile info. You need to be owner of the profile.

### [DELETE] /api/v1/profiles/{profileId : int}

Delete profile. You need to be owner of the profile.

### [POST] /api/v1/profiles/{profileId : int}/follows

Change follow state. You cannot follow yourself. You cannot set the same state twice (You cannot follow already followed profile).

### [GET] /api/v1/profiles/{profileId : int}/follows

Get list of all profiles that follow given profile.

### [GET] /api/v1/photos

Get list of all photos.

### [POST] /api/v1/photos

Upload your own photo with description.

### [GET] /api/v1/photos/{photoId : int}

Get photo with given ID.

### [PATCH] /api/v1/photos/{photoId : int}

Update description for specified photo. You have to be owner of this photo.

### [DELETE] /api/v1/photos/{photoId : int}

Delete photo. You have to be owner of this photo.

### [GET] /api/v1/photos/{photoId : int}/likes

Get list of profiles that like this photo.

### [POST] /api/v1/photos/{photoId : int}/likes

Change like state. You can like your own photos. You cannot set the same state twice (You cannot like already liked photo).

### [GET] /api/v1/photos/{photoId : int}/comments

Get list of all comments related to the photo.

### [POST] /api/v1/photos/{photoId : int}/comments

Add new comment to the photo.

### [DELETE] /api/v1/photos/{photoId : int}/comments/{commentId : int}

Delete comment. You have to be owner of comment. commentId has to be related to given photoId otherwise you will get error response.

### [GET] /api/v1/photos/profile/{profileId : int}

Get list of all photos uploaded by given profile.

### [GET] /api/v1/photos/profile/{profileId : int}/followers

Get list of photos that belongs to profiles followed by given profileId.

### [GET] /images/{imageName : str}

Get image. Path with image name can be obtained from endpoints that return photo info. There is no JSON response from this endpoint.
