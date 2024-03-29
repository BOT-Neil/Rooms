<br/>
<p align="center">
  <h3 align="center">Rooms</h3>

  <p align="center">
    An addon plugin for AdvancedSlimePaper forks
    <br/>
    <br/>
    <a href="https://github.com/BOT-Neil/Rooms">View Demo</a>
    .
    <a href="https://github.com/BOT-Neil/Rooms/issues">Report Bug</a>
    .
    <a href="https://github.com/BOT-Neil/Rooms/issues">Request Feature</a>
  </p>
</p>

![Downloads](https://img.shields.io/github/downloads/BOT-Neil/Rooms/total) ![Contributors](https://img.shields.io/github/contributors/BOT-Neil/Rooms?color=dark-green) ![Issues](https://img.shields.io/github/issues/BOT-Neil/Rooms) ![License](https://img.shields.io/github/license/BOT-Neil/Rooms)

## Table Of Contents

* [About the Project](#about-the-project)
* [Built With](#built-with)
* [Getting Started](#getting-started)
    * [Prerequisites](#prerequisites)
    * [Installation](#installation)
* [Usage](#usage)
* [Roadmap](#roadmap)
* [Contributing](#contributing)
* [License](#license)
* [Authors](#authors)
* [Acknowledgements](#acknowledgements)

## About The Project

![Screen Shot](//images/screenshot.png)


## Built With

gradle, java 17

## Getting Started

Build with ./gradlew clean build shadowjar

### Prerequisites

AdvancedSlimePaper   
WorldGuard   
FAWE


### Installation

1.Install ASP+plugin and setup your config as wanted  
2.Add FastAsyncWorldEdit and WorldGuard plugins.  
3.Optionally, I would recommend you use mysql instead of sqlite  
4.Optionally migrate your plotsquared plots via the admin setting menu,
my storage went from 100gb untrimmed to under 200mb for 10000 plots although alot were empty and had no grass.  



## Usage

Use '/rooms' command to get started   
DON'T MIGRATE YOUR PLOTSQUARED PLOTs WITHOUT BACKING UP EVERYTHING, EVERYTHING!  

_For more examples, please refer to the [Documentation](https://github.com/BOT-Neil/Rooms/wiki)_

## Roadmap

*Finish Java GUI's  
*Finish Bedrock forms  
*Finish Commands  
*Finish Redis and implement matchmaking   
*Implement presets   
*Make Text configurable   
*Hex support to text config aswell   
*island support after biomes are fixed upstream   
*possibly change end/nether island UUID to standard UUID format instead of suffix, apparently better index performance  

## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.
* If you have suggestions for adding or removing projects, feel free to [open an issue](https://github.com/BOT-Neil/Rooms/issues/new) to discuss it, or directly create a pull request after you edit the *README.md* file with necessary changes.
* Please make sure you check your spelling and grammar.
* Create individual PR for each suggestion.
* Please also read through the [Code Of Conduct](https://github.com/BOT-Neil/Rooms/blob/main/CODE_OF_CONDUCT.md) before posting your first idea as well.

### Creating A Pull Request

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

Distributed under the GPL-V3 License. See [LICENSE](https://github.com/BOT-Neil/Rooms/blob/main/LICENSE.md) for more information.

## Authors

* **Neil** - ** - [Neil](https://github.com/BOT-NEIL) - **
* **AdvancedSlimePaper** - ** - [AdvancedSlimePaper](https://github.com/InfernalSuite/AdvancedSlimePaper) - **
* **FastAsyncWorldEdit** - ** - [FastAsyncWorldEdit](https://www.spigotmc.org/resources/fastasyncworldedit.13932/) - **
* **WorldGuard** - ** - [WorldGuard](https://builds.enginehub.org/job/worldguard?branch=version/7.0.x) - **

## Acknowledgements

* [ShaanCoding](https://github.com/ShaanCoding/)
* [Othneil Drew](https://github.com/othneildrew/Best-README-Template)
* [ImgShields](https://shields.io/)
