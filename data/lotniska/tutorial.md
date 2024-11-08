# Wczytanie lotnisk z pliku

1. Dodaj plik lotniskaDane.csv do poniżego folderu:

![alt text](mySQLDataFolder.png)

jego ścieżka:

"C:\ProgramData\MySQL\MySQL Server 8.0\Data\lotniskaDane.csv"

2. Zezwól na wczytywanie danych z pliku do tabel w mysql:

    a) otwórz notatnik z uprawnianiami admina

    ![alt text](adminNotatnik.png)

    b) otwórz w nim plik o podanej ścieżce na Windowsie:
    C:\ProgramData\MySQL\MySQL Server 8.0\my.ini

    lub na linux któryś z nich:
    /etc/mysql/my.cnf lub /etc/my.cnf

    c) wyszukaj [mysql] i [mysqld]

    ![alt text](myIni.png)
    
    dodaj w odpowiednie miejsca:
    local_infile=1
```
    [mysql]
    local_infile=1

    [mysqld]
    local_infile=1
```
    d) zresetuj mysql cmd
3. W mysql Command Line Client:

    a) 
    use database multitripfinder;

    b) jeśli jeszcze nie masz tabeli z lotniskami wykonaj:

    ```
    CREATE TABLE miastazlotniskami (
    KodLotniska VARCHAR(3),
    KodMiasta VARCHAR(3),
    NazwaLotniska VARCHAR(255),
    Miasto VARCHAR(255),
    Kraj VARCHAR(255)
    );
    ```

    c) wpisz lotniska z pliku csv do tabeli:


    ```
    LOAD DATA LOCAL INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Data/lotniskaDane.csv'
    INTO TABLE lotniska
    FIELDS TERMINATED BY ',' 
    ENCLOSED BY '"'
    LINES TERMINATED BY '\r\n';
    ```
    UWAGA!!!:  '\r\n' jest bardzo ważne, bo na końcu kazdej linijki jest znak CR(Carriage Return)

    d) przetestuj:
    ```
    SELECT * FROM lotniska WHERE Kraj = 'Poland';
    ```c