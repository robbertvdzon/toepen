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
                    gepauzeerd: {{tafel.gepauzeerd}}, winnaar: {{tafel.tafelWinnaar!=null?getSpelerNaam(tafel.tafelWinnaar):"-"}}
                </div>
                <hr>
            </div>

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
            getSpeler: function (spelerId) {
                var spelerCount = this.speldata.alleSpelers.length;
                for (var i = 0; i < spelerCount; i++) {
                    if (this.speldata.alleSpelers[i].id == spelerId) {
                        return this.speldata.alleSpelers[i];
                    }
                }
            },
            getSpelerNaam: function (spelerId) {
                var speler = this.getSpeler(spelerId)
                if (speler==null) return "?"
                return speler.naam
            },            maakTafels: function (event) {
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
