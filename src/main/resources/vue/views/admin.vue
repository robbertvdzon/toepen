<template id="admin">
    <app-frame>
       <opdracht-tabs-frame></opdracht-tabs-frame>
       <div id="content">
        <table  v-if="speldata">
            <thead>
            <tr>
                <th>ID</th>
                <th>Naam</th>
                <th>wilMeedoen</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="speler in speldata.alleSpelers" >
                <td><a @click="window.open('/speler/'+speler.id, '_blank');" >{{speler.id}}</a></td>
                <td><input v-model="speler.naam"></td>
                <td><input v-model="speler.wilMeedoen"></td>
            </tr>
            </tbody>
        </table>
        <span>
              <button type="submit" v-on:click="loadData">Load</button>
              <button type="submit" v-on:click="saveData">Save</button>
        </span>

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
           <tr>
               <td>vulTafelsAanMetMonkeysTot</td>
               <td>&nbsp;:&nbsp;</td>
               <td><input v-model="vulTafelsAanMetMonkeysTot"></td>
           </tr>
       </table>

        <span>
              <button type="submit" v-on:click="maakTafels">Maak tafels</button>
        </span>

       <div  v-if="speldata">
           <tbody>
           <div v-for="tafel in speldata.tafels" >
               <h2>Speeltafel {{tafel.tafelNr}}</h2>
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
                   </tr>
                   </tbody>
               </table>
           </div>
           </tbody>
       </div>


    </div>
    </app-frame>
</template>

<script>

    Vue.component("admin", {
        template: "#admin",
        data: () => ({
            speldata: null,
            aantalTafels: 1,
            aantalStartLucifers: 5,
            vulTafelsAanMetMonkeysTot:3
        }),
        created() {
            this.load()
        },

        methods: {
            load: function (event) {
                fetch(`/api/speldata`)
                    .then(res => res.json())
                    .then(json =>
                        this.speldata = json
                    )
                    .catch(() => alert("Error while fetching opdracht"));

                // gebruik websockets voor de volgende updates
                let ws = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat");
                ws.onmessage = msg => this.speldata = JSON.parse(msg.data);
                ws.onclose = () => location.reload();
            },
            checkresult: function (res) {
                if (res.data.status=="FAILED") {
                    alert(res.data.errorMessage)
                }
            },
            loadData: function (event) {
                axios.post(`/api/load`,null)
                    .then(res => this.checkresult(res))
            },
            saveData: function (event) {
                axios.post(`/api/save`,this.speldata)
                    .then(res => this.checkresult(res))
            },
            maakTafels: function (event) {
                axios.post(`/api/maaktafels/`+this.aantalTafels+`/`+this.aantalStartLucifers+`/`+this.vulTafelsAanMetMonkeysTot,null)
                    .then(res => this.checkresult(res))
            },

        }
    });
</script>
