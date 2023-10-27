# ServiceApp
My first pet project for cv with kotlin

It's just a shell of some app that can be created
Realized:
-MVVM pattern
-Hilt
-Dagger
-Room
-Full firebase (Google social network and phone number auth)
-Custom Alert Dialogs
-Navigation component
-Coroutines
-Glide
-Image cropper

Project navigation:
1) User discovers login screen, where he should choose one of two buttons: google or phone number.
2) User enter his phone number.
3) Firebase services sends user otp code to enter in special field, checks if phone number exists and belongs to him.
4) After that programm checks if user already exists in firebase by phone number.
5) If it's true, user just immidiately reach the main screen, but if not true, then the user should enter his data to registration fragment.
6) Right after he enter all the data and pass all the verification processes he reaches the main screen with all other possiblities such as "add advertisment" in "submit fragment", "check advertisments" in "advertisment fragment", "change user data stored in firebase" in "account fragment" or even "change language or other things" in "settings fragment".

It works not ideal, but it is what it is, my first own pet-project :)
