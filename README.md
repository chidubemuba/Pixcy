Photo Album App Design Project - README Template
===

# PIXCY

## Table of Contents
1. [Overview](#Overview)
2. [Product Spec](#Product-Spec)
3. [Wireframes](#Wireframes)
<!--4. [Schema](#Schema)-->

## Overview
### Description
This app allows the user to sign up or log into their account using Facebook. User can take pictures showing the locations of the pictures taken on a map.

### App Evaluation
- **Category:** Photo
- **Mobile:** This app would be primarily developed for mobile because of the need to access a phone's camera.
- **Story:** Allows users to document their life and experiences in the form of pictures and gives them a visualizaton of those memories on a map using the picture capture location.
- **Market:** Anyone that loves taking pictures and wants to keep track of all the places they have visited could really enjoy this app. 
- **Habit:** User can post throughtout the day as many times as possible. It also helps the users to form a very good life documenting habit. 
- **Scope:** First we will start by allowing the users to take and post pictures and view them on their homepage. Then the user can view all the pictures taken on a map based on the location of each picture taken.

## Product Spec

### 1. User Features (Required and Optional)

**Required Must-have Features**
* [x] User signs up or logs into their account using Facebook or Google firedb.
* [x] Create a database which holds multiple users records that can be accessed using their account username or email and password verification.
* [x] User can take pictures using their phone camera, add a caption/description, and post it on the app. These posts will be saved on the created database.
* [x] User can pull down to refresh posts on their memories page
* [x] The current signed in user is persisted across app restarts
* [x] User can log out of their account.
* [x] Use the View Binding library to reduce view boilerplate.
* [x] User can tap a post to go to a Post Details activity, which includes timestamp and caption.
* [x] User should switch between different tabs using fragments and a Bottom Navigation View.
* [x] User can view the location of each picture taken on a map
* [x] User can pinch to scale (zoom in/out) the location of each picture on the map
* [x] User will have various view experiences as they pinch to scale the location of the pictures on maps

**Optional Nice-to-have features**
* [x] Improve the runtime speed of my application as a complex feature.
* [x] Newly taken pictures will be automatically inserted into the user own memories page without relying on a manual refresh.
* [ ] Improve user interface by experimenting with styling and coloring as well as applying rounded corners for the image post using glide transformation
* [ ] Allow user to make, upload videos, and view videos on their feed.
* [ ] Allow the logged in user to add a profile photo
* [x] Display the user's post in a cardview layout

### 2. Screen Archetypes

* [Login screen]
   * User can sign in using Facebook authentication

* [Main screen with bottom navigation and Fragments]
   * [Memories Fragment]
       * User should see all their pictures
   * [Camera Fragment]
        * User should be able to take pictures using their phone camera
   * [Compose Fragment]
       * User can take picture and write a brief description and post it to memory
   * [Map Fragment]
        * User will see the location of each picture taken
   * [Logout button]
       * User can logout from their account

### 3. Navigation

**Tab Navigation** (Tab to Screen)
* login
* Memories
* Map
* Camera
* Compose

**Flow Navigation** (Screen to Screen)
* [login]
  * Facebook Log-in -> Account creation if no log in is available -> Profile
* [Memories]
  * Click on a post in memories -> Post details
* [Map]
  * Click on an icon on map -> Icon details
* [Camera]
  * Camera -> Compose
* [Compose]
  * Compose -> Memories

## Wireframes
 * [Link to wireframe](https://www.figma.com/file/ms5OgcKA0x5n8AORNSbOE5/Untitled?node-id=0%3A1)

<!--## Schema 
[This section will be completed in Unit 9]
### Models
[Add table of models]
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]-->
