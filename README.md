# Portfolio: Kevyt projektinhallintamalli

Tämä on esimerkki kevytprojektinhallintamallista, jossa tiimit ja projektit toimivat domain-aggregaatteina. Projektin tavoitteena on havainnollistaa domain-keskeistä arkkitehtuuria, jossa liiketoimintasäännöt asuvat aggregaateissa, ei serviceissä.


## Tavoite

- Rakentaa testattava ja ymmärrettävä DDD-pohjainen malli.
- Korostaa domainin eheyttä (esim. TimeEstimation ei ole vain `int`, vaan ValueObject).
- Näyttää miten write- ja read-mallit voidaan erottaa kevyesti.
- Näyttää miten Spring Data JDBC tukee immutable-struktuuria
- Käyttää tapahtumia (esim. `TeamTaskCompletedEvent`) tilan päivittämiseen aggregaattien välillä.


## Teknologiat

- Java 17
- Maven
- PostreSQL
- Docker
- Spring Boot
- Spring Data JDBC
- JUnit 5 + Mockito

## Peruskäyttö

Sovellus vaatii PostgreSQL:n, joka ajetaan Dockerin kautta. docker-compose.yml on konfiguroitu seuraavasti:

### .env-tiedosto (luo juureen)
POSTGRES_PASSWORD=salasana123

PGDATA_VOLUME=/c/Users/demo/postgres-data

#### PostreSql-kontin käynnistys
```docker compose up -d```

### Spring-boot 
Sovellus olettaa env-muuttujista löytyvän tietokannan salasanan. Sen voi Windows/cmd-promptissa asettaa esimerkiksi näin ```set POSTGRES_PASSWORD=salasana123```
Itse sovellus käynnistetään project-demo-kansiossa ajamalla komento ```mvn spring-boot:run```

## Domainin rakenne

- **Project**: Omistaa alkuarvion (InitialEstimation), projektille lisätään tehtäviä. Projekti itse huolehtii tehtäviä lisättäessä, että arvioitu aika-arvio ei ylity
- **Team**: Omistaa tiimin jäsenet ja vastaa tehtävien hallinnasta.
- **TeamTask**: Edustaa tiimin työyksikköä ja säilyttää elinkaaren sekä mahdollisesti toteutuneen ajan (`ActualTimeSpent`).

## Value Objectit

- **TimeEstimation**: Abstraktoi ajan arvion. Estää virheelliset arvot (esim. negatiiviset tunnit).
- **ActualSpentTime**: Kuvaa oikeasti kulunutta aikaa. Voi päivittyä vasta kun task on valmis.
- **ProjectId, ProjectTaskId, ContactPersonId, TaskId, TeamId, TeamTaskId, TeamMemberId**: Varmistavat oikeat ID-käytännöt ilman paljaita merkkijonoja tai UUID:itä.


## Eventit

Tietyt aggregaattitapahtumat laukaisevat muita päivityksiä järjestelmässä:

- `TaskAddedToProjectEvent`: syntyy, kun uusi taski lisätään projektille, käsittelijä lähettää tästä sähköpostia projektin yhteyshenkilölle. Tämä demonstroi "side-effect":in käsittelyä
- `TeamTaskCompletedEvent`: kun tiimi merkitsee tehtävän valmiiksi, tämän eventin käsittelijä päivittää projektin vastaavan taskin valmiiksi toteutuneen työmäärän kanssa. Projekti itse huolehtii itse siitä, että projekti merkitään valmiiksi jos kaikki sen tehtävät ovat valmiita. Tämän eventin käsittely demonstroi DDD:n perusperiaatetta, että kahta aggregate roottia ei saa tallentaa yhdessä transaktiossa. Eventin käsittely on myös idempotentti. Jos sen käsittelyn aikana tapahtuu optimisisen lukituksen virhe, yritetään uudestaan. Jos toinen osapuoli on yrittänyt lisätä tehtävää, tarkistetaan onko projekti jo valmis

## REST-endpointit (esimerkit)
todo

## Rajoitteet ja huomiot

- Tämä projekti demonstroi lähinnä DDD ja Spring Data JDBC-osaamista. Siinä ei ole toteutettu mm. oikeaa autentikoitumista

## Kehittäjä

Toteuttanut Tapio Niemelä. Portfolio toimii todisteena osaamisesta:

Java + Spring Boot + Spring Data JDBC

Domain Driven Design (aggregaatit, säännöt, eventit)

Testivetoisuus

Käytännöllinen REST-rajapinta
