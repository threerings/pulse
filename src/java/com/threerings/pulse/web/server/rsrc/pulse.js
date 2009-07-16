function pulse(records) {
    var data = {};
    var chart;
    var knownServers = {};
    var servers = new panopticon.ui.CheckBoxes("Servers", 'servers', []);
    var selectors = panopticon.transform(records, function(recordType, fields) {
        if (fields.length == 0) {
            return;
        }
        var select = new panopticon.ui.MultiSelect(recordType, recordType, fields);
        function getData(field, checked) {
            var fullName = recordType + "." + field;
            if (checked && !data.hasOwnProperty(fullName)) {
                // Stick an empty list in there to keep us from trying again while waiting
                data[fullName] = [];
                var params = {
                    record : recordType,
                    field : field
                };
                $.getJSON("", params, function(results) {
                        panopticon.each(results.records, function(record) {
                                if (!knownServers[record[0]]) {
                                    knownServers[record[0]] = true;
                                    servers.names.push([record[0], record[0]]);
                                    panopticon.fragment.get(servers.id, []);
                                    $("#" + servers.id).replaceWith(servers.makeHtml());
                                    chart.resetControlInput();
                                }
                            });
                        data[fullName] = results.records;
                        if (chart) {
                            chart.plotLater();
                        }
                    });
            }
        }
        select.extract = function() {
            var result = panopticon.ui.MultiSelect.prototype.extract.call(select);
            panopticon.each(result, getData);
            return result;
        };
        select.makeHtml = function() {
            var result = panopticon.ui.MultiSelect.prototype.makeHtml.call(select);
            panopticon.each(select.value, getData);
            return result;
        };
        return select;
    });
    var controls = selectors.slice();
    controls.push(servers);
    chart = new panopticon.chart.Chart(function() {
        var collector = new panopticon.Collector();
        panopticon.each(selectors, function(selector) {
            panopticon.each(selector.value, function(field, selected) {
                if (!selected) {
                    return;
                }
                if (!data.hasOwnProperty(selector.id + "." + field)) {
                    return;
                }
                panopticon.each(data[selector.id + "." + field], function(record) {
                        if (servers.value[record[0]]) {
                            collector.assume(field + " " + record[0]).push([record[1], record[2]]);
                        }
                });
            });
        });
        return collector.toValues();
    }, {
        controls: controls,
        xaxis: {
            mode: "time"
        }
    });

}
