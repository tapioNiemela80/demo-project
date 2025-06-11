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
- PostgreSQL
- Docker
- Spring Boot
- Spring Data JDBC
- JUnit 5 + Mockito

## Peruskäyttö

Sovellus vaatii PostgreSQL:n, joka ajetaan Dockerin kautta. docker-compose.yml on konfiguroitu seuraavasti:

### .env-tiedosto (luo juureen)
POSTGRES_PASSWORD=salasana123

PGDATA_VOLUME=/c/Users/demo/postgres-data

#### PostrgeSql-kontin käynnistys
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
- `TeamTaskCompletedEvent`: kun tiimi merkitsee tehtävän valmiiksi, tämän eventin käsittelijä päivittää projektin vastaavan taskin valmiiksi toteutuneen työmäärän kanssa. Projekti itse huolehtii itse siitä, että projekti merkitään valmiiksi jos kaikki sen tehtävät ovat valmiita. Tämän eventin käsittely demonstroi DDD:n perusperiaatetta, että kahta aggregate roottia ei saa tallentaa yhdessä transaktiossa. Eventin käsittely on myös idempotentti. Jos sen käsittelyn aikana tapahtuu optimistisen lukituksen virhe, yritetään uudestaan. Jos puolestaan toinen osapuoli on yrittänyt lisätä tehtävää, tarkistetaan onko projekti jo valmis ja hylätään sen aiheuttama päivitys (jos projekti on jo valmis)

## REST-endpointit (esimerkit)

### Luo projekti
```curl --location 'http://localhost:8080/projects' --header 'Content-Type: application/json' --data-raw '{"name":"coding project", "description":"portfolio demonstration", "estimatedEndDate": "2026-01-01", "estimation":{"hours":10,"minutes":55}, "contactPersonInput":{"name":"tapio niemelä","email":"tapio.niemela_1@yahoo.com"}}'```

### Lisää taski projektille
```curl --location 'http://localhost:8080/projects/cd8a4243-717b-4181-bb5a-83381f511920/tasks' --header 'Content-Type: application/json' --data '{"name":"java code", "description":"make java code demonstrating ddd and spring data jdbc", "estimation":{"hours":8, "minutes":0}}'```

### Lisää tiimi
```curl --location 'localhost:8080/teams' --header 'Content-Type: application/json' --data '{"name":"ddd and spring data jdbc demonstration team"}'```

### Lisää tiimille jäsen
```curl --location 'localhost:8080/teams/791031a6-922b-4ea0-93da-ae7b21a7a09b/members' --header 'Content-Type: application/json' --data '{"name":"tapio niemelä", "profession":"ddd enthuistic"}'```

## Rajoitteet ja huomiot

- Tämä projekti demonstroi lähinnä DDD ja Spring Data JDBC-osaamista. Siinä ei ole toteutettu mm. oikeaa autentikoitumista tai minkäänlaista käyttöliittymää
- Tavoitteena on ollut pitää aggregate-malli keskittyneenä toimintoihin (write). Tietojen hakeminen(read) on toteutettu erikseen suorilla SQL-kyselyillä. Read-malli on tehty kevyesti, koska se ei ole oleellinen osa demoa
- Yksikkötestit on tehty vain kriittisille toiminnallisuuksille

## Kehittäjä

- Toteuttanut Tapio Niemelä. Portfolio toimii todisteena osaamisesta:
- Java + Spring Boot + Spring Data JDBC
- Domain Driven Design (aggregaatit, säännöt, eventit)
- Testivetoisuus
- Käytännöllinen REST-rajapinta
