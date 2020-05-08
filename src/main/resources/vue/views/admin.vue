<template id="admin">
    <app-frame>
        <a href="toepkingspelers" >[SPELERS]</a>
        <div id="content">

            <h2>Nieuwe tafels aanmaken:</h2>
            <table v-if="speldata">
                <tr>
                    <td>Aantal tafels</td>
                    <td>&nbsp;:&nbsp;</td>
                    <td><input v-model="aantalTafels"></td>
                </tr>
                <tr>
                    <td>Startscore</td>
                    <td>&nbsp;:&nbsp;</td>
                    <td><input v-model="aantalStartLucifers"></td>
                </tr>
            </table>
            <button type="submit" v-on:click="maakTafels">Maak tafels</button>

            <hr>
            <h2>Acties:</h2>

            <span>
              <button type="submit" v-on:click="allesPauzeren">Alles pauze</button>
              <button type="submit" v-on:click="allesStarten">Alles starten</button>
              <button type="submit" v-on:click="clearLog">Clear log</button>
              <button type="submit" v-on:click="resetScore">Reset score</button>
            </span>

            <hr>
            <h2>Automaat settings:</h2>

            <table v-if="speldata">
                <tr>
                    <td>Automatisch nieuwe tafels</td>
                    <td>&nbsp;:&nbsp;</td>
                    <td><input type="checkbox" v-model="speldata.automatischNieuweTafels"></td>
                </tr>
                <tr>
                    <td>Automatisch pauze bij nieuwe tafels</td>
                    <td>&nbsp;:&nbsp;</td>
                    <td><input type="checkbox" v-model="speldata.nieuweTafelAutoPause"></td>
                </tr>
                <tr>
                    <td>Aantal nieuwe tafels</td>
                    <td>&nbsp;:&nbsp;</td>
                    <td><input v-model="speldata.aantalAutomatischeNieuweTafels"></td>
                </tr>
                <tr>
                    <td>Aantal fisches</td>
                    <td>&nbsp;:&nbsp;</td>
                    <td><input v-model="speldata.aantalFishesNieuweTafels"></td>
                </tr>
                <tr>
                    <td>Monkey delay</td>
                    <td>&nbsp;:&nbsp;</td>
                    <td><input v-model="speldata.monkeyDelayMsec"></td>
                </tr>
            </table>
            <button type="submit" v-on:click="saveSettings">Save Settings</button>

            <hr>
            <h2>Tafels:</h2>

            <div v-if="speldata">
                <div v-for="tafel in speldata.tafels">
                    Speeltafel {{tafel.tafelNr}}
                    <button type="submit" v-on:click="pauzeer(tafel.tafelNr)">Pauzeer</button>
                    <button type="submit" v-on:click="gadoor(tafel.tafelNr)">Ga door</button>
                    <button type="submit" v-on:click="nieuwspel(tafel.tafelNr, aantalStartLucifers)">Nieuw spel</button>
                    <button type="submit" v-on:click="nieuwspel(tafel.tafelNr, 999)">Tijdelijk spel</button>
                    <button type="submit" v-on:click="schopTafel(tafel.tafelNr)">Schop</button>
                    <button type="submit" v-on:click="dumpTafel(tafel.tafelNr)">Dump</button>
                    gepauzeerd: {{tafel.gepauzeerd}}, winnaar: {{tafel.tafelWinnaar!=null?tafel.tafelWinnaar.naam:"-"}}
                </div>
                <hr>
            </div>

            <div v-if="speldata">
                <div v-for="tafel in speldata.tafels">
                    <h2>Speeltafel {{tafel.tafelNr}}</h2>

                    <table>
                        <tr>
                            <td>Gepauzeerd :</td>
                            <td>{{tafel.gepauzeerd}}</td>
                        </tr>
                        <tr>
                            <td>Huidige speler :</td>
                            <td>{{tafel.huidigeSpeler!=null?tafel.huidigeSpeler.naam:"-"}}</td>
                        </tr>
                        <tr>
                            <td>Opkomer :</td>
                            <td>{{tafel.opkomer!=null?tafel.opkomer.naam:"-"}}</td>
                        </tr>
                        <tr>
                            <td>Toeper :</td>
                            <td>{{tafel.toeper!=null?tafel.toeper.naam:"-"}}</td>
                        </tr>
                        <tr>
                            <td>Inzet :</td>
                            <td>{{tafel.inzet}}</td>
                        </tr>
                        <tr>
                            <td>SlagWinnaar :</td>
                            <td>{{tafel.slagWinnaar!=null?tafel.slagWinnaar.naam:"-"}}</td>
                        </tr>
                        <tr>
                            <td>TafelWinnaar :</td>
                            <td>{{tafel.tafelWinnaar!=null?tafel.tafelWinnaar.naam:"-"}}</td>
                        </tr>
                    </table>
                    <table>
                        <thead>
                        <tr>
                            <th>Speler</th>
                            <th>totaalLucifers</th>
                            <th>actiefInSpel</th>
                            <th>ingezetteLucifers</th>
                            <th>gepast</th>
                            <th>toepKeuze</th>
                            <th>gespeeldeKaart</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr v-for="speler in tafel.spelers">
                            <td>{{speler.naam}}</td>
                            <td>{{speler.totaalLucifers}}</td>
                            <td>{{speler.actiefInSpel}}</td>
                            <td>{{speler.ingezetteLucifers}}</td>
                            <td>{{speler.gepast}}</td>
                            <td>{{speler.toepKeuze}}</td>
                            <td>{{speler.gespeeldeKaart}}</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>

            <hr>
            <h2>Uitslagen</h2>
            <table v-if="speldata">
                <tr v-for="uitslag in speldata.uitslagen">
                    <td valign="top">{{uitslag.timestamp}}</td>
                    <td valign="top">Tafel:{{uitslag.tafel}}</td>
                    <td valign="top">
                        <table>
                        <tr v-for="log in uitslag.logregels">
                            <td>{{log.naam}}</td>
                            <td>{{log.score}}</td>
                        </tr>
                        </table>

                    </td>
                </tr>
            </table>

        </div>
    </app-frame>
</template>

<script>

    Vue.component("admin", {
        template: "#admin",
        data: () => ({
            speldata: null,
            aantalTafels: 3,
            aantalStartLucifers: 10
        }),
        created() {
            this.load()
        },

        methods: {
            loaded: function (json) {
                this.speldata = json;
                this.aantalTafels = this.speldata.tafels.length
            },
            load: function (event) {
                fetch(`/api/speldata`)
                    .then(res => res.json())
                    .then(json => this.loaded(json)
                    )
                    .catch(() => alert("Error while fetching opdracht"));

            },
            checkresult: function (res) {
                if (res.data.status == "FAILED") {
                    alert(res.data.errorMessage)
                } else {
                    alert("Done");
                }
            },
            saveSettings: function (event) {
                axios.post(`/api/savesettings`, this.speldata)
                    .then(res => this.checkresult(res))
            }
            ,loadData: function (event) {
                axios.post(`/api/load`, null)
                    .then(res => this.checkresult(res))
            },
            maakTafels: function (event) {
                axios.post(`/api/maaktafels/` + this.aantalTafels + `/` + this.aantalStartLucifers, null)
                    .then(res => this.checkresult(res))
            },
            allesPauzeren: function () {
                axios.post(`/api/allespauzeren`, null).then(res => this.checkresult(res))
            },
            allesStarten: function () {
                axios.post(`/api/allesstarten`, null).then(res => this.checkresult(res))
            },
            pauzeer: function (tafelnr) {
                axios.post(`/api/pauzeer/` + tafelnr, this.speldata).then(res => this.checkresult(res))
            },
            gadoor: function (tafelnr) {
                axios.post(`/api/gadoor/` + tafelnr, this.speldata).then(res => this.checkresult(res))
            },
            nieuwspel: function (tafelnr, aantalStartLucifers) {
                axios.post(`/api/nieuwspel/` + tafelnr + `/` + aantalStartLucifers, this.speldata).then(res => this.checkresult(res))
            },
            schopTafel: function (tafelnr) {
                axios.post(`/api/schop/` + tafelnr, this.speldata).then(res => this.checkresult(res))
            },
            dumpTafel: function (tafelnr) {
                axios.post(`/api/dump/` + tafelnr, this.speldata).then(res => this.checkresult(res))
            },
            clearLog: function () {
                axios.post(`/api/clearlog`, null).then(res => this.checkresult(res))
            },
            resetScore: function () {
                axios.post(`/api/resetscore`, null).then(res => this.checkresult(res))
            },

        }
    });
</script>
