package com.example.richardsenyange.breedlocator2;

class Users {
    private String email;
    private String name;
    private String contact;
    private   String user_category;

    Users(){
    }

    Users(String email, String name, String contact,String user_category) {
        this.email = email;
        this.name = name;
        this.contact = contact;
        this.user_category=user_category;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
    public String getUser_category() {return user_category;}
    public void setUser_category(String user_category) {this.user_category = user_category;}
}
