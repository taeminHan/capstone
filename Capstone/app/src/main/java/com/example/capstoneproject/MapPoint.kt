package com.example.capstoneproject

class MapPoint {
    var name: String? = null
    var latitude = 0.0
    var longitude = 0.0

    constructor() : super() {}
    constructor(Name: String?, latitude: Double, longitude: Double) {
        //super();
        name = Name
        this.latitude = latitude
        this.longitude = longitude
    }
}