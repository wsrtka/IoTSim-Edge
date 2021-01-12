# IoTSIM

To reduce the complexity of the code, we used lombok in the pom.xml which provides getter and setter for different variables automatically. Based on the version of the Eclipse, lombok version need to updated. If this does not work, please download the jar file from  https://projectlombok.org/downloads/lombok.jar and install using command line > java -jar lombok.jar. Finally restart the Eclipse to make the change effective.

The IoTSim-Edge codebase is dependent on CloudSim which is provided as external jar file located in EdgeIoTSim\lib folder. One has to change the location of CloudSim jar file to compile succesfully.

## Dokumentacja

### Założenia projektu

Celem projektu było rozszerzenie symulatora IoTSimEdge o dane o nasłonecznieniu z bazy danych BSRN i utworzenie prostej symulacji z udziałem panelu słonecznego. W ramach projektu dodany został pakiet *panels*, przykład jego użycia oraz klasy opisane niżej.

### Dodane klasy

#### SolarPanel
Klasa reprezentująca panel fotowoltaiczny. Posiada kluczowe atrybuty panelu: *efektywność (efficiency)* oraz *powierznię (area)*. Ponadto dodatkowo panel przechowuje listę urządzeń, do których dostarcza prąd (*suppliedDevices*), *baterię (battery)*, do której przesyłana jest nadmiarowa energia, *szybkość transferu energii (transportSpeed)* oraz opisane niżej *strategię (strategy)* oraz *logger (log)*.

Dostępne metody:

*setPowerDistributionStrategy* - ustawienie strategii zasilania urządzeń przez panel.

*connect* - podłączenie urządzenia do panelu poprzez dodanie do listy podłączonych urządzeń.

*disconnect* - odłączenie urządzenia od zasilania przez panel słoneczny.

*supplyEnergy* - obliczenie wyprodukowanej energii oraz dystrybuowanie jej pomiędzy wszystkie podłączone urządzenia (wraz z baterią), w zależności od ustawionej strategii zasilania.

*getCurrentPowerOutput* - obliczenie wyprodukowanej energii na podstawie promieniowania słonecznego, temperatury otoczenia oraz kąta pomiędzy powierzchnią panelu i promieni słonecznych. Do obliczenia energii produkowanej przez panel użyty został wzór z pracy *Optimal Bidding Strategy for Microgrids Considering Renewable Energy and Building Thermal Dynamics* autorstwa Duong Tung Nguyen i Long Bao Le, 2014. Korekta ze względu na nachylenie została napisana na podstawie [strony ftexploring.com](https://www.ftexploring.com/solar-energy/sun-angle-and-insolation3.htm).

*getCurrentBatteryCapacity* - zwraca aktualną pojemność baterii podłączonej do panelu.

#### DataReader
Odpowiada za odczyt danych o  z pliku .tab. Metoda getData(Calendar date) przegląda plik z danymi poszukując wiersza odpowiadającego danemu dniu i czasowi. Po znalezieniu odpowiedniego wiersza zwraca dane o nasłonecznieniu w postaci pary liczb.

#### PowerDistributionStrategy
Interfejs dla strategii dystrybucji energii wytworzonej przez panel słoneczny pomiędzy jego baterią a podłączonymi urządzeniami.

Posiada jedną metodę, *distributePower*, która wykonuje wszystkie operacje ładowania i niczego nie zwraca.

#### PowerDistributionVerboseStrategy
Abstrakcyjna klasa opisująca strategie dystrybucji mocy z wykorzystaniem szczegółowych wypisów do konsoli.

Dostępne metody:

*distributePower* - abstrakcyjna metoda, której zadaniem będzie wykonanie operacji ładowania zgodnie z wybraną strategią

*distributePowerToDevices* - rozdziela energię równo między listą urządzeń i pozwala wybrać, czy do ładowania użyć energii ze słońca czy słońca i baterii; unika przeładowania każdego z urządzeń, ogranicza szybkość przesyłu energii do wspólnej dla wszystkich urządzeń podanej wartości.

*chargeSolarBattery*, *chargeDeviceFromSun*, *chargeDeviceFromBattery* - ładują pojedyncze urządzenia, oraz wypisują wyniki do konsoli.

*announceLeftoverPower* - wypisanie informacji o ilości pozostałej energii.

#### PowerBatteryFirst
Strategia priorytetyzująca ładowanie baterii wbudowanej do panelu słonecznego. Dba o to, aby bateria zawsze była ładowana najszybciej jak się da, a pozostałą energię wykorzystuje do ładowania podłączonych urządzeń. Nie pozwala na ładowanie urządzeń z baterii panelu słonecznego.

#### PowerDevicesFirst
Strategia skupiona na ładowaniu baterii urządzeń. Gdy każde z urządzeń jest ładowane z maksymalną prędkością lub ma pełną baterię, przystępuje do łądowania baterii panelu. Gdy brak energii słonecznej, wykorzystuje baterię panelu do ładowania urządzeń. 

#### Logger
Niestatyczna klasa loggera pozwalająca na zapis do kilku plików jednocześnie.

#### SolarExample
Klasa zawierająca symulację działania całego układu (urządzenia IoT ładowanego przy pomocy panelu słonecznego).
