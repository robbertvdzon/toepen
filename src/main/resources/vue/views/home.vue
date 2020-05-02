<template id="home">
    <app-frame>
        <br><br><br>
        <table>
            <tr>
                <td>Inlogcode</td>
                <td>&nbsp;:&nbsp;</td>
                <td><input v-model="inlogcode"></td>
            </tr>
        </table>
        <br><br><br>
        <span>
              <button type="submit" v-on:click="start">Start</button>
        </span>
    </app-frame>
</template>
<script>


    Vue.component("home", {
        template: "#home",
        data: () => ({
            inlogcode: ""
        }),
        created() {
        },
        methods: {
            start: function (event) {
                location.href = "speler/"+this.inlogcode;
            }
        }
    });
</script>
