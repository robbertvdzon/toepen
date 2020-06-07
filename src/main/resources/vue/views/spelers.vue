<template id="spelers">
    <app-frame>
        <a href="toepking" >[ADMIN]</a>
        <div id="content">
            <table v-if="speldata">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Naam</th>
                    <th>Score</th>
                    <th>wilMeedoen</th>
                    <th>Monkey</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="speler in speldata.alleSpelers">
                    <td><a @click="window.open('/speler/'+speler.id, '_blank');">{{speler.id}}</a></td>
                    <td><input v-model="speler.naam"></td>
                    <td><input v-model="speler.score"></td>
                    <td><input type="checkbox" v-model="speler.wilMeedoen"></td>
                    <td><input type="checkbox" v-model="speler.isMonkey"></td>

                </tr>
                </tbody>
            </table>
            <span>
              <button type="submit" v-on:click="loadData">Load</button>
              <button type="submit" v-on:click="saveData">Save</button>
            </span>
        </div>
    </app-frame>
</template>

<script>

    Vue.component("spelers", {
        template: "#spelers",
        data: () => ({
            speldata: null,
            aantalTafels: 1,
            aantalStartLucifers: 15
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
            loadData: function (event) {
                axios.post(`/api/load`, null)
                    .then(res => this.checkresult(res))
            },
            saveData: function (event) {
                axios.post(`/api/save`, this.speldata)
                    .then(res => this.checkresult(res))
            }

        }
    });
</script>
