<template id="overzicht">
    <div id="content">
        <header>
            <img src="/cards.png" height="100px">
            <div class="headerdiv">
                Vriendjes Toep Toernooi
            </div>
        </header>
        <div v-if="speldata">
            <div v-for="mytafel in speldata.tafels">
                <div>
                    <div class="statusbalk">
                        {{getTafelStatus(mytafel)}}
                    </div>
                </div>

                <table>
                    <tr>
                        <td v-for="speler in mytafel.spelers" width="250px">

                            <div style="z-index: 1; position: relative; height: 70px">
                                <div style="z-index: 1; position: absolute; top: 20px; left: 0px;">
                                    <span>
                                    <b v-if="isAanZet(getSpeler(speler), mytafel)" class="naamAanBeurt"><u>{{getSpelerNaam(speler)}}</u></b>
                                    <b v-if="!isAanZet(getSpeler(speler), mytafel)" class="naamNietBeurt">{{getSpelerNaam(speler)}}</b>
                                    <img src="/monkey.png" height="40px" v-if="isMonkey(getSpeler(speler))">
                                    </span>

                                </div>
                                <div style="z-index: 2; position: absolute; top: 0px; left: 0px;"
                                     v-if="getoept(getSpeler(speler))">
                                    <img src="/getoept.png" height="50px">
                                </div>
                                <div style="z-index: 2; position: absolute; top: 0px; left: 0px;"
                                     v-if="gaatMee(getSpeler(speler))">
                                    <img src="/gaatmee.png" height="50px">
                                </div>
                                <div style="z-index: 2; position: absolute; top: 0px; left: 0px;" v-if="gepast(getSpeler(speler))">
                                    <img src="/gepast.png" height="50px">
                                </div>
                                <div style="z-index: 2; position: absolute; top: 0px; left: 0px;" v-if="isAf(getSpeler(speler))">
                                    <img src="/af.png" height="50px">
                                </div>
                            </div>
                        </td>
                    </tr>
                    <tr style="height: 20px">
                        <td v-for="speler in mytafel.spelers">
                            <img src="/fiche.png" height="15px"
                                 v-for="lucifer in numToArrayRij1(getSpeler(speler).totaalLucifers-getSpeler(speler).ingezetteLucifers)">
                        </td>
                    </tr>
                    <tr style="height: 20px">
                        <td v-for="speler in mytafel.spelers">
                            <img src="/fiche.png" height="15px"
                                 v-for="lucifer in numToArrayRij2(getSpeler(speler).totaalLucifers-getSpeler(speler).ingezetteLucifers)">
                            {{overigeLucifers(getSpeler(speler).totaalLucifers-getSpeler(speler).ingezetteLucifers)}}
                        </td>
                    </tr>
                    <tr style="height: 10px">
                    </tr>
                    <tr style="height: 20px">
                        <td v-for="speler in mytafel.spelers">
                            <img src="/fiche.png" height="30px"
                                 v-for="lucifer in numToArray(getSpeler(speler).ingezetteLucifers)">
                        </td>
                    </tr>
                    <tr height="100px">
                        <td v-for="speler in mytafel.spelers">
                            <div class="kaartenred">{{getRodeKaart(getSpeler(speler).gespeeldeKaart)}}</div>
                            <div class="kaartenblack">{{getZwarteKaart(getSpeler(speler).gespeeldeKaart)}}</div>
                        </td>
                    </tr>
                </table>
                <br><br><br><br>
            </div>

            <table >
                <thead>
                <tr>
                    <th>Naam</th>
                    <th>Score</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="speler in speldata.scorelijst">
                    <td>{{speler.naam}}</td>
                    <td>{{speler.score}}</td>
                </tr>
                </tbody>
            </table>


        </div>


    </div>
</template>

<script>

    Vue.component("overzicht", {
        template: "#overzicht",
        data: () => ({
            speldata: null,
        }),
        created() {
            this.load()
        },

        methods: {
            loadedData(json) {
                this.speldata = json;
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
                let ws = new WebSocket("ws://" + location.hostname + ":" + location.port + "/game");
                ws.onmessage = msg => this.loadedData(JSON.parse(msg.data));
                ws.onclose = () => location.reload();

            },
            checkresult: function (res) {
                if (res.data.status == "FAILED") {
                    this.error = res.data.errorMessage;
                    setTimeout(() => {
                        this.clearError()
                    }, 1500);
                }
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
            isAanZet: function (speler, mytafel) {
                return mytafel.huidigeSpeler != null && (speler.id == mytafel.huidigeSpeler);

            },
            isMonkey: function (speler) {
                return speler.isMonkey;
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
            getTafelStatus: function (mytafel) {
                if (mytafel.tafelWinnaar != null) {
                    return "Tafel:"+mytafel.tafelNr+":Spel afgelopen, " + this.getSpelerNaam(mytafel.tafelWinnaar) + " heeft gewonnen!";
                }
                if (mytafel.gepauzeerd) {
                    return "Tafel:"+mytafel.tafelNr+":Spel is gepauzeerd!";
                }
                return "Tafel:"+mytafel.tafelNr+":"+this.getSpelerNaam(mytafel.huidigeSpeler) + " is aan de beurt";
            },
            getSpelWinnaar: function (mytafel) {
                if (mytafel == null) return "-";
                if (mytafel.tafelWinnaar == null) return "-";
                return this.getSpelerNaam(mytafel.tafelWinnaar);
            },
            numToArray: function (nr) {
                var elements = [];
                for (i = 0; i < nr; i++) elements.push("1");
                return elements;
            },
            numToArrayRij1: function (nr) {
                var elements = [];
                var total = nr;
                if (total > 8) total = 8;
                for (i = 0; i < total; i++) elements.push("1");
                return elements;
            },
            numToArrayRij2: function (nr) {
                var elements = [];
                var total = nr - 8;
                if (total > 8) total = 8;
                for (i = 0; i < total; i++) elements.push("1");
                return elements;
            },
            overigeLucifers: function (nr) {
                var elements = [];
                var total = nr - 16;
                if (total > 0) return "+" + total;
                return ""
            },
            getoept: function (speler) {
                if (!speler.actiefInSpel) return false;
                if (speler.gepast) return false;
                return (speler.toepKeuze == "TOEP")
            },
            gaatMee: function (speler) {
                if (!speler.actiefInSpel) return false;
                if (speler.gepast) return false;
                return (speler.toepKeuze == "MEE")
            },
            gepast: function (speler) {
                if (!speler.actiefInSpel) return false;
                return speler.gepast
            },
            isAf: function (speler) {
                return !speler.actiefInSpel;
            },
        }
    });
</script>
