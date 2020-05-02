<template id="speler">
    <app-frame>
       <opdracht-tabs-frame></opdracht-tabs-frame>
       <div id="content">
           <div v-if="error">
               ERROR: {{error}}
           </div>
           <div v-if="tafel" >
               <h2>Speeltafel {{tafel.tafelNr}}, {{speler.naam}}</h2>
               <table >
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
               <table >
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
                   <tr v-for="speler in tafel.spelers" >
                       <td>{{speler.naam}}</td>
                       <td>{{speler.totaalLucifers}}</td>
                       <td>{{speler.actiefInSpel}}</td>
                       <td>{{speler.ingezetteLucifers}}</td>
                       <td>{{speler.gepast}}</td>
                       <td>{{speler.toepKeuze}}</td>
                       <td>{{speler.gespeeldeKaart}}</td>
                       <td v-if="tafel.huidigeSpeler!=null && (speler.naam==tafel.huidigeSpeler.naam)"><---------</td>
                   </tr>
                   </tbody>
               </table>
           </div>

           <br><br>
           <b><u>Hand:</u></b>
           <div v-if="speler" >
               <div v-for="(kaart, idx) in speler.kaarten" >
                   {{kaart.symbool}} {{kaart.waarde}}
                   <button type="submit" v-on:click="speelKaart(idx)">Speel</button>
               </div>
               <br>
               <br>
               <button type="submit" v-on:click="pakSlag">Pak slag</button>
               <button type="submit" v-on:click="toep">Toep</button>
               <button type="submit" v-on:click="gaMee">Ga mee</button>
               <button type="submit" v-on:click="pas">Pas</button>

           </div>

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
            id:null,
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
                        if (speler.id==this.id){
                            this.speler = speler;
                            this.tafel = tafel;
                        }
                    }
                }
                if (this.speler==null){
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
                ws.onclose = () => alert("WebSocket connection closed");

            },
            checkresult: function (res) {
                if (res.data.status=="FAILED") {
                    alert(res.data.errorMessage)
                }
            },
            speelKaart: function (kaart) {
                axios.post(`/api/speelkaart/`+this.id,this.speler.kaarten[kaart])
                     .then(res => this.checkresult(res))
            },
            pakSlag: function (kaart) {
                axios.post(`/api/pakslag/`+this.id,this.speler.kaarten[kaart])
                     .then(res => this.checkresult(res))
            },
            pas: function (kaart) {
                axios.post(`/api/pas/`+this.id,this.speler.kaarten[kaart])
                     .then(res => this.checkresult(res))
            },
            toep: function (kaart) {
                axios.post(`/api/toep/`+this.id,this.speler.kaarten[kaart])
                     .then(res => this.checkresult(res))
            },
            gaMee: function (kaart) {
                axios.post(`/api/gamee/`+this.id,this.speler.kaarten[kaart])
                     .then(res => this.checkresult(res))
            },
        }
    });

</script>
