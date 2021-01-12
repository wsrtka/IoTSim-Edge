# IoTSIM

To reduce the complexity of the code, we used lombok in the pom.xml which provides getter and setter for different variables automatically. Based on the version of the Eclipse, lombok version need to updated. If this does not work, please download the jar file from  https://projectlombok.org/downloads/lombok.jar and install using command line > java -jar lombok.jar. Finally restart the Eclipse to make the change effective.

The IoTSim-Edge codebase is dependent on CloudSim which is provided as external jar file located in EdgeIoTSim\lib folder. One has to change the location of CloudSim jar file to compile succesfully.

## Documentation

### Project goals

The goal of our project was to extent the IoTSimEdge simulator to include irradiance data from the BSRN database and create a simple simulation of a device powered by a solar panel.
We added the *panels* package, an example and the classes described below.

This project was part of a 2020/2021 winter semester IoT course at the Faculty of Computer Science, Electronics and Telecommunications of AGH University of Science and Technology, Kraków.

[//]: <> (Celem projektu było rozszerzenie symulatora IoTSimEdge o dane o nasłonecznieniu z bazy danych BSRN i utworzenie prostej symulacji z udziałem panelu słonecznego. W ramach projektu dodany został pakiet *panels*, przykład jego użycia oraz klasy opisane niżej.)

### Added Classes

#### SolarPanel

Klasa reprezentująca panel fotowoltaiczny. Posiada kluczowe atrybuty panelu: *efektywność (efficiency\)* oraz *powierznię (area\)*. Ponadto dodatkowo panel przechowuje listę urządzeń, do których dostarcza prąd (*suppliedDevices*\), *baterię (battery\)*, do której przesyłana jest nadmiarowa energia, *szybkość transferu energii (transportSpeed\)* oraz opisane niżej *strategię (strategy\)* oraz *logger (log\)*.

Dostępne metody:

*setPowerDistributionStrategy* - ustawienie strategii zasilania urządzeń przez panel.

*connect* - podłączenie urządzenia do panelu poprzez dodanie do listy podłączonych urządzeń.

*disconnect* - odłączenie urządzenia od zasilania przez panel słoneczny.

*supplyEnergy* - obliczenie wyprodukowanej energii oraz dystrybuowanie jej pomiędzy wszystkie podłączone urządzenia (wraz z baterią), w zależności od ustawionej strategii zasilania.

*getCurrentPowerOutput* - obliczenie wyprodukowanej energii na podstawie promieniowania słonecznego, temperatury otoczenia oraz kąta pomiędzy powierzchnią panelu i promieni słonecznych. Do obliczenia energii produkowanej przez panel użyty został wzór z pracy *Optimal Bidding Strategy for Microgrids Considering Renewable Energy and Building Thermal Dynamics* autorstwa Duong Tung Nguyen i Long Bao Le, 2014. Korekta ze względu na nachylenie została napisana na podstawie [strony ftexploring.com](https://www.ftexploring.com/solar-energy/sun-angle-and-insolation3.htm\).

*getCurrentBatteryCapacity* - zwraca aktualną pojemność baterii podłączonej do panelu.

#### DataReader
Odpowiada za odczyt danych o  z pliku .tab. Metoda *getData* przegląda plik z danymi poszukując wiersza odpowiadającego danemu dniu i czasowi. Po znalezieniu odpowiedniego wiersza zwraca dane o nasłonecznieniu w postaci pary liczb.

#### PowerDistributionStrategy
An common interface for strategies of distributing the power produced by a solar pannel between its built-in battery and connected devices.

I includes one method, *distributePower*, which is tasked with completing all charging operations and returns nothing.

[//]: <> (Interfejs dla strategii dystrybucji energii wytworzonej przez panel słoneczny pomiędzy jego baterią a podłączonymi urządzeniami.)
[//]: <> (Posiada jedną metodę, *distributePower*, która wykonuje wszystkie operacje ładowania i niczego nie zwraca.)

#### PowerDistributionVerboseStrategy
And abstract class describing a power distribution strategy using verbose console outputs.

Available methods:

*distributePower* - abstract method, which will implement strategy-specific power distribution logic.

*distributePowerToDevices* - evenly distributes power between a given list of devices. 
You can select if charging should use solar energy and the solar panel's battery, or just solar energy.
Prevents overcharging and recursively redistributes excess power between all connected devices.

*chargeSolarBattery*, *chargeDeviceFromSun*, *chargeDeviceFromBattery* - charges a single device or battery 
and outputs the charge amount as well as current battery capacity.

*announceLeftoverPower* - outputs the leftover power that was not used while charging.

[//]: <> (Abstrakcyjna klasa opisująca strategie dystrybucji mocy z wykorzystaniem szczegółowych wypisów do konsoli.)

[//]: <> (Dostępne metody:)

[//]: <> (*distributePower* - abstrakcyjna metoda, której zadaniem będzie wykonanie operacji ładowania zgodnie z wybraną strategią)

[//]: <> (*distributePowerToDevices* - rozdziela energię równo między listą urządzeń 
i pozwala wybrać, czy do ładowania użyć energii ze słońca czy słońca i baterii; 
unika przeładowania każdego z urządzeń, 
ogranicza szybkość przesyłu energii do wspólnej dla wszystkich urządzeń podanej wartości.)

[//]: <> (*chargeSolarBattery*, *chargeDeviceFromSun*, *chargeDeviceFromBattery* - ładują pojedyncze urządzenia, oraz wypisują wyniki do konsoli.)

[//]: <> (*announceLeftoverPower* - wypisanie informacji o ilości pozostałej energii.)

#### PowerBatteryFirst
Power distribution strategy that prioritizes charging the solar battery. It will not use solar battery to power devices. Devices are only charged when battery is full or charging at the maximum speed.

[//]: <> (Strategia priorytetyzująca ładowanie baterii wbudowanej do panelu słonecznego. Dba o to, aby bateria zawsze była ładowana najszybciej jak się da, a pozostałą energię wykorzystuje do ładowania podłączonych urządzeń. Nie pozwala na ładowanie urządzeń z baterii panelu słonecznego.)

#### PowerDevicesFirst
Power distribution strategy that prioritizes charging the device batteries. It will use battery power to charge device batteries if they are not charging at maximum speed. It will only charge the solar battery if each device is either fully charged or charging at the maximum rate.

[//]: <> (Strategia skupiona na ładowaniu baterii urządzeń. Gdy każde z urządzeń jest ładowane z maksymalną prędkością lub ma pełną baterię, przystępuje do łądowania baterii panelu. Gdy brak energii słonecznej, wykorzystuje baterię panelu do ładowania urządzeń. )

#### Logger
Niestatyczna klasa loggera pozwalająca na zapis do kilku plików jednocześnie.

#### SolarExample
Klasa zawierająca symulację działania całego układu (urządzenia IoT ładowanego przy pomocy panelu słonecznego\).

### Porównanie strategii zasilania
Poniższe wykresy przedstawiają zależność energii baterii urządzenia, energii baterii panelu słonecznego oraz energii wytwarzanej przez panel ze słońca od czasu dla symulacji przeprowadzonej dla 4 dni pracy urządzeń. Dla zachowania czytelności skala wykresu została zaniedbana (pojemność baterii urządzenia jest dużo większa od baterii panelu\).

![wykres](./img/strategy_comparison.png)

Jak widać na wykresie w przypadku użycia strategii *BatteryFirst* na początku symulacji bateria panelu została naładowana do swojej maksymalnej pojemności, którą utrzymała przez cały okres trwania symulacji. Natomiast w przypadku zastosowania strategii *DevicesFirst* bateria panelu przez większość czasu trwania symulacji jest całkowicie rozładowana. Jest ona ładowana tylko wtedy gdy panel w jednostce czasu wytwarza więcej energii niż jest w stanie przekazać do urządzenia. Plusem tego podejścia jest to, że bateria urządzenia ma przez większość czasu trwania symulacji więcej energii niż przy użyciu poprzedniej strategii, co widać na wykresach.
