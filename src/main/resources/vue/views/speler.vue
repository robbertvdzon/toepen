<template id="speler">
    <app-frame>
        <opdracht-tabs-frame></opdracht-tabs-frame>

        <div id="content">
            <div v-if="error">
                ERROR: {{error}}
            </div>
            <div v-if="tafel">
                <h2>
                    <table>
                        <tr>
                            <td>Speeltafel:{{tafel.tafelNr}}</td>
                            <td>Speler:{{speler.naam}}</td>
                            <td>slagwinnaar:{{getSlagWinnaar()}}</td>
                            <td>spelwinnaar:{{getSpelWinnaar()}}</td>
                        </tr>
                    </table>
                </h2>
                <hr>
                <table>
                    <tr>
                        <td v-for="speler in tafel.spelers"  width="250px">
                            <b v-if="isAanZet(speler)" class="naamNietBeurt"><u>{{speler.naam}}</u></b>
                            <b v-if="!isAanZet(speler)" class="naamNietBeurt">{{speler.naam}}</b>
                        </td>
                    </tr>
                    <tr>
                        <td v-for="speler in tafel.spelers">
                            score:{{speler.totaalLucifers}}
                            <br>
                            inzet:{{speler.ingezetteLucifers}}
                            <br>
                            {{getStatus(speler)}}
                        </td>
                    </tr>
                    <tr height="100px">
                        <td v-for="speler in tafel.spelers">
                            <div class="kaartenred">{{getRodeKaart(speler.gespeeldeKaart)}}</div>
                            <div class="kaartenblack">{{getZwarteKaart(speler.gespeeldeKaart)}}</div>
                        </td>
                    </tr>
                </table>
            </div>
            <hr>

            <br><br>
            <div v-if="speler&&!slagGewonnen()&&!toepKeuzeDoorgeven()">
                <div>
                    <table>
                        <tr>
                            <td>
                                <img src="/toep.png" height="60px" v-on:click="toep">
                            </td>
                            <td v-for="(kaart, idx) in speler.kaarten" width="150px">
                                <div class="kaartenred" v-on:click="speelKaart(idx)">{{getRodeKaart(kaart)}}</div>
                                <div class="kaartenblack" v-on:click="speelKaart(idx)">{{getZwarteKaart(kaart)}}</div>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
            <img v-if="slagGewonnen()" src="/pakslag.png" height="60px" v-on:click="pakSlag">
            <img v-if="toepKeuzeDoorgeven()" src="/gamee.png" height="60px" v-on:click="gaMee">
            <img v-if="toepKeuzeDoorgeven()" src="/pas.png" height="60px" v-on:click="pas">

        </div>
    </app-frame>
</template>

<script>


    Vue.component("speler", {
        template: "#speler",
        data: () => ({
            speldata: null,
            tafel: null,
            speler: null,
            id: null,
            error: null
        }),
        created() {
            this.load()
        },

        methods: {
            loadedData(json) {
                this.speldata = json;
                this.speler = null;
                this.tafel = null;
                this.error = null;

                var tafelCount = this.speldata.tafels.length;
                for (var i = 0; i < tafelCount; i++) {
                    tafel = this.speldata.tafels[i];
                    var spelerCount = tafel.spelers.length;
                    for (var j = 0; j < spelerCount; j++) {
                        speler = tafel.spelers[j];
                        if (speler.id == this.id) {
                            this.speler = speler;
                            this.tafel = tafel;
                        }
                    }
                }
                if (this.speler == null) {
                    this.error = "Geen tafel gevonden";
                }
            },
            load: function (event) {
                this.id = this.$javalin.pathParams["id"];
                // initieel de gegevens ophalen
                fetch(`/api/speldata`)
                    .then(res => res.json())
                    .then(json =>
                        this.loadedData(json)
                    )
                    .catch(() => alert("Error while fetching opdracht"));

                // gebruik websockets voor de volgende updates
                let ws = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat");
                ws.onmessage = msg => this.loadedData(JSON.parse(msg.data));
                ws.onclose = () => location.reload();

            },
            checkresult: function (res) {
                if (res.data.status == "FAILED") {
                    alert(res.data.errorMessage)
                }
            },
            speelKaart: function (kaart) {
                axios.post(`/api/speelkaart/` + this.id, this.speler.kaarten[kaart])
                    .then(res => this.checkresult(res))
            },
            pakSlag: function (kaart) {
                axios.post(`/api/pakslag/` + this.id, this.speler.kaarten[kaart])
                    .then(res => this.checkresult(res))
            },
            pas: function (kaart) {
                axios.post(`/api/pas/` + this.id, this.speler.kaarten[kaart])
                    .then(res => this.checkresult(res))
            },
            toep: function (kaart) {
                axios.post(`/api/toep/` + this.id, this.speler.kaarten[kaart])
                    .then(res => this.checkresult(res))
            },
            gaMee: function (kaart) {
                axios.post(`/api/gamee/` + this.id, this.speler.kaarten[kaart])
                    .then(res => this.checkresult(res))
            },
            getWaarde: function (kaart) {
                if (kaart == null) return;
                if (kaart.waarde == "7") return "B";
                if (kaart.waarde == "8") return "V";
                if (kaart.waarde == "9") return "H";
                if (kaart.waarde == "10") return "A";
                if (kaart.waarde == "11") return "7";
                if (kaart.waarde == "12") return "8";
                if (kaart.waarde == "13") return "9";
                if (kaart.waarde == "14") return "10";
                return "?";
            },
            getRodeKaart: function (kaart) {
                if (kaart == null) return;
                if (kaart.symbool == "KLAVER") return;
                if (kaart.symbool == "SCHOPPEN") return;
                if (kaart.symbool == "HARTEN") symbool = "♥"
                if (kaart.symbool == "RUITEN") symbool = "♦"
                return symbool + this.getWaarde(kaart);
            },
            getZwarteKaart: function (kaart) {
                if (kaart == null) return;
                if (kaart.symbool == "HARTEN") return;
                if (kaart.symbool == "RUITEN") return;
                if (kaart.symbool == "KLAVER") symbool = "♣"
                if (kaart.symbool == "SCHOPPEN") symbool = "♠"
                return symbool + this.getWaarde(kaart);
            },
            isAanZet: function (speler) {
                return tafel.huidigeSpeler != null && (speler.naam == tafel.huidigeSpeler.naam);

            },
            getStatus: function (speler) {
                if (!speler.actiefInSpel) return "Af";
                if (speler.gepast) return "Gepast";
                if (speler.toepKeuze == "GEEN_KEUZE") return "";
                if (speler.toepKeuze == "NVT") return "";
                if (speler.toepKeuze == "PAS") return "Pas";
                if (speler.toepKeuze == "MEE") return "Ga mee";
                if (speler.toepKeuze == "TOEP") return "Toep!";
                return ""
            },
            slagGewonnen: function () {
                if (this.tafel == null) return false;
                if (this.speler == null) return false;
                if (this.tafel.slagWinnaar == null) return false;
                return (this.tafel.slagWinnaar.naam == this.speler.naam)
            }
            ,
            toepKeuzeDoorgeven: function () {
                if (this.tafel == null) return false;
                if (this.speler == null) return false;
                if (this.tafel.toeper == null) return false;
                if (this.speler.toepKeuze != "GEEN_KEUZE") return false;
                return (this.tafel.huidigeSpeler.naam == this.speler.naam)
            },
            getSlagWinnaar: function (){
                if (this.tafel == null) return "-";
                if (this.tafel.slagWinnaar == null) return "-";
                return this.tafel.slagWinnaar.naam;

            },
            getSpelWinnaar: function (){
                if (this.tafel == null) return "-";
                if (this.tafel.tafelWinnaar == null) return "-";
                return this.tafel.tafelWinnaar.naam;
            },
        }
    });
</script>
