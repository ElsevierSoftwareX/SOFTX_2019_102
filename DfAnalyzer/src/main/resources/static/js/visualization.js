/**
 * @author Thaylon Guedes
 * @email thaylongs@gmail.com
 */
var network;
var app = new Vue({
    el: '#app',
    data: {
        attMap: {},
        selectedElemet: null
    }, methods: {
        loadGraph: function () {
            var context = this;
            $.ajax({
//                url: '/dfa/api/dataflows/' + getUrlParameters("dfId", "", true),
                url: '/api/dataflows/' + getUrlParameters("dfId", "", true),
                type: 'GET',
                success: function (result) {
                    context.attMap = result["allAttrsMap"];
                    context.plotGraph(result["graph"]);
                }
            });
        },
        plotGraph: function (data) {
            var container = document.getElementById('mynetwork');
            var options = {interaction: {hover: true}};
            var context = this;
            network = new vis.Network(container, data, options);
            network.on("click", function (params) {
                if (params.nodes.length > 0) {
                    var id = params.nodes[0];
                    context.selectedElemet = app.attMap[id];
                }
            });
        }
    }
});
app.loadGraph();

//Utils Area
/**
 *
 * @param {String} parameter
 * @param {String} staticURL
 * @param {bollean} decode
 * @returns {String|Boolean}
 */
function getUrlParameters(parameter, staticURL, decode) {
    /*
     Function: getUrlParameters
     Description: Get the value of URL parameters either from
     current URL or static URL
     Author: Tirumal
     URL: www.code-tricks.com
     */
    try {
        var currLocation = (staticURL.length) ? staticURL : window.location.search,
                parArr = currLocation.split("?")[1].split("&"),
                returnBool = true;

        for (var i = 0; i < parArr.length; i++) {
            parr = parArr[i].split("=");
            if (parr[0] == parameter) {
                if (returnBool)
                    returnBool = true;
                return (decode) ? decodeURIComponent(parr[1]) : parr[1];
            } else {
                returnBool = false;
            }
        }

        if (!returnBool)
            return false;
    } catch (err) {
        return null;
    }
}