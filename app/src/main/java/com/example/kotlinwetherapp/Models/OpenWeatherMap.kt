package com.example.kotlinwetherapp.Models

class OpenWeatherMap{
    var weather: List<Weather>? = null
    var clouds: Clouds? = null
    var coord: Coord? = null
    var main: Main? = null

    var wind:Wind? = null
    var sys: Sys? = null
    var base: String? = null
    var name: String? = null
    var cod: Int? = 0
    var dt: Int? = 0
    var id: Int? = 0

    constructor()

    constructor(weather: List<Weather>, clouds: Clouds, coord: Coord, base: String, name: String, main: Main,
                wind:Wind, sys: Sys, cod: Int, dt: Int, id: Int)
    {
        this.weather = weather
        this.clouds = clouds
        this.coord = coord
        this.base = base
        this.name = name
        this.main = main
        this.wind = wind
        this.sys = sys
        this.cod = cod
        this.dt = dt
        this.id = id
    }
}