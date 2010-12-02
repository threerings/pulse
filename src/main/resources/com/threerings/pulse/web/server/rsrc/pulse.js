function pulse(records) {
    eval(bedrock.include({
                'bedrock':['gibs'],
                'bedrock.util':['log'],
                'bedrock.iter':['each'],
                'bedrock.collections':['List', 'Dict', 'ListDict', 'Set']
            }));
    var data = new Dict();
    var chart;
    var knownServers = new Set();
    var servers = new panopticon.ui.CheckBoxes("Servers", 'servers', []);
    var selectors = new List(records, function(recordType, fields) {
        if (fields.length == 0) {
            return;
        }
        var select = new panopticon.ui.MultiSelect(recordType, recordType, fields);
        function getData(field, checked) {
            var fullName = recordType + "." + field;
            if (checked && !data.has(fullName)) {
                // Stick an empty list in there to keep us from trying again while waiting
                data.put(fullName, []);
                var params = {
                    record : recordType,
                    field : field
                };
                function update() {
                    if (!select.has(field)) {
                        return;
                    }
                    $.getJSON("", params, function(results) {
                            var existing = data.get(fullName);
                            each(results.records, function(record) {
                                    // Update later requests to start at our last point
                                    if (params.start === undefined || record[1] > params.start) {
                                        params.start = record[1] + 1;
                                    }
                                    if (knownServers.add(record[0])) {
                                        servers.names.push([record[0], record[0]]);
                                        gibs.get(servers.id, []);
                                        $("#" + servers.id).replaceWith(servers.makeHtml());
                                        chart.resetControlInput();
                                    }
                                    existing.push(record);
                                });
                        if (chart) {
                            chart.plotLater();
                        }
                    });
                }
                setInterval(update, 3 * 60 * 1000);
                update();
            }
        }
        select.extract = function() {
            var result = panopticon.ui.MultiSelect.prototype.extract.call(select);
            each(result, getData);
            return result;
        };
        select.makeHtml = function() {
            var result = panopticon.ui.MultiSelect.prototype.makeHtml.call(select);
            each(select.value, getData);
            return result;
        };
        return select;
    });
    var controls = selectors.slice();
    controls.push(servers);
    chart = new panopticon.chart.Chart(function() {
            var collector = new ListDict();
            selectors.each(function(selector) {
                    selector.value.each(function(field) {
                            if (!data.has(selector.id + "." + field)) {
                                return;
                            }
                            each(data.get(selector.id + "." + field),
                                function(record) {
                                    if (servers.has(record[0])) {
                                        var value = [record[1], record[2]];
                                        collector.assume(field + " " + record[0]).push(value);
                                    }
                                });
                        });
                });
            return collector.items(function(key, value) {
                    return {label: key, data:value}
                });
        }, {
            controls: controls,
            xaxis: {
                mode: "localtime"
            }
        });

}
