Toep toernooi
--
Er zijn 2 beheerpagina's:
http://localhost:7000/toepking (hier kun je nieuwe tafels aanmaken en tafels pauzeren of weer starten)
en
http://localhost:7000/toepkingspelers (hier kun je nieuwe spelers aanmaken, en een speler als 'monkey' instellen (dan is er een monkey die voor deze speler speelt))

Daarnaast is er een overzicht pagina van alle tafels op:
http://localhost:7000/overzicht

Elke speler heeft een eigen ID. En met dat ID kan elke speler zijn eigen pagina openen en hier spelen, bijvoorbeeld:
http://localhost:7000/speler/27331

Er zit geen enkele beveiliging in deze applicatie en als je dit thuis start en wilt delen met vrienden, dan moet je zelf portforwarding inregelen op de router.

Gebruikte technieken
--
- Kotlin
- Vavr
- Javalin
- Vue (via Javalin en webjars)
- Websockets

Verbeterpunten:
--
- Gebruikers kunnen toevoegen en verwijderen (nu een vaste lijst)
- Vuile was implementeren
- Naast de ReplayTest nog een betere test coverage
- Authenticatie voor admin en gebruikers toevoegen

