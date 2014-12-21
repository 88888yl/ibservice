/**
 * Created by 305028327 on 2014/11/20.
 */
searchByPN();

function getParameter(paraName) {
    var sUrl = location.href + "?para1=invest&para2=dollar";
    var reg = "(?:\\?|&){1}" + paraName + "=([^&]*)";
    var regresult = new RegExp(reg, "gi");
    regresult.exec(sUrl);
    return RegExp.$1;
}

function searchByPN() {
    var partnumber = getParameter("partnumber");
    var description = getParameter("description");
    var mep = getParameter("mep");
    var rdo = getParameter("rdo").split("?")[0];

    $.ajax({
        type: 'post',
        dataType: 'text',
        async: true,
        url: 'search_pn.action',
        data: {pn: partnumber,
            desc: description,
            mep: mep,
            rdo: rdo
        },
        success: function (result) {
            if (result == "fail") {
                alert("PN is not found!");
            } else {
                result = eval("(" + result + ")");
                initTable(result);
            }
        },
        error: function () {
            $("#result").html("Search failed!");
        }
    });
}

function initTable(result) {
    var table = document.createElement("table");
    table.border = "1px";
    table.padding = 0;
    table.margin = 0;
    table.borderColor = "black";
    table.setAttribute("id", "tblContent");

    var tr = document.createElement("tr");
    tr.style.backgroundColor = "#99CCCC";
    tr.bold;

    var td = document.createElement("td");
    td.innerHTML = "PN#";
    tr.appendChild(td);

    td = document.createElement("td");
    td.innerHTML = "Revision";
    tr.appendChild(td);

    td = document.createElement("td");
    td.innerHTML = "State";
    tr.appendChild(td);

    td = document.createElement("td");
    td.innerHTML = "Description";
    tr.appendChild(td);

    td = document.createElement("td");
    td.innerHTML = "MEP";
    tr.appendChild(td);

    td = document.createElement("td");
    td.innerHTML = "MFG";
    tr.appendChild(td);

    td = document.createElement("td");
    td.innerHTML = "RDO";
    tr.appendChild(td);

    td = document.createElement("td");
    td.innerHTML = "Product";
    tr.appendChild(td);

    document.getElementById("div1").appendChild(table);
    table.appendChild(tr);
    addTr(result, table);
}

function addTr(result, table) {

    var a = result;
    var k = 0;
    for (var i = 0; i < a.length; i++) {
        for (var j = 0; j < a[i].length; j++) {
            k++;
            var tr = document.createElement("tr");

            var pn_value = document.createTextNode(a[i][j].name);
            var revision_value = document.createTextNode(a[i][j].revision);
            var state_value = document.createTextNode(a[i][j].state);
            var desc_value = document.createTextNode(a[i][j].description);
            var mep_value = document.createTextNode(a[i][j].manufacturer_Equivalent_part);
            var mfg_value = document.createTextNode(a[i][j].manufacturer);
            var rdo_value = document.createTextNode(a[i][j].RDO);
            var product = document.createTextNode(a[i][j].usedByWho);

            var td1 = document.createElement("td");
            td1.appendChild(pn_value);
            tr.appendChild(td1);

            td1 = document.createElement("td");
            td1.appendChild(revision_value);
            tr.appendChild(td1);

            td1 = document.createElement("td");
            td1.appendChild(state_value);
            tr.appendChild(td1);

            td1 = document.createElement("td");
            td1.appendChild(desc_value);
            tr.appendChild(td1);

            td1 = document.createElement("td");
            td1.appendChild(mep_value);
            tr.appendChild(td1);

            td1 = document.createElement("td");
            td1.appendChild(mfg_value);
            tr.appendChild(td1);

            td1 = document.createElement("td");
            td1.appendChild(rdo_value);
            tr.appendChild(td1);

            td1 = document.createElement("td");
            td1.appendChild(product);
            tr.appendChild(td1);

            table.appendChild(tr);

            if (k % 2 == 0) tr.style.backgroundColor = "#99CCCC";
            else tr.style.backgroundColor = "#CCFFCC";
        }
    }
    cells_color();
    dbclick(result);
}

function cells_color() {
    $("#tblContent").find("tr").mouseover(function () {
        if ($(this).index() == 0)
            return;
        $(this).css({ "background-color": "#FFFFCC" });
    }).mouseout(function () {
        var $index = $(this).index();
        if ($index % 2 == 0) {
            $(this).css({ "background-color": "#99CCCC" });
        } else {
            $(this).css({ "background-color": "#CCFFCC" });
        }
    });
}

function dbclick(result) {

    $("#tblContent").find("tr").click(function () {
        document.getElementById("table1").innerHTML = "";
        var pn = $(this).children("td").eq(0).html();
        var whereuse = "";
        var n = 0;

        for (var i = 0; i < result.length; i++) {
            for (var j = 0; j < result[i].length; j++) {
                if (pn == result[i][j].name) {
                    n++;
                    whereuse += result[i][j].usedByWho + ", ";
                    break;
                }
            }
        }
        var answer = "PN#" + pn + " Used in [<span style=\"color:red;font-size: 18px;\"> "
            + n + " </span>] Product: " + whereuse.substr(0, whereuse.length - 2);
        document.getElementById("table1").innerHTML = answer;
    });
}