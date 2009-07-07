function pulse(records) {
    var data = {};
    var chart;
    var selectors = panopticon.transform(records, function(recordType, fields) {
        if (fields.length == 0) {
            return;
        }
        var checks = new panopticon.ui.CheckBoxes(recordType, recordType,
                fields, true, false);
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
                            data[fullName] = results.records;
                            if (chart) {
                                chart.plot();
                            }
                        });
            }
        }
        checks.extract = function() {
            var result = panopticon.ui.CheckBoxes.prototype.extract.call(checks);
            panopticon.each(result, getData);
            return result;
        };
        checks.makeHtml = function() {
            var result = panopticon.ui.CheckBoxes.prototype.makeHtml.call(checks);
            panopticon.each(checks.value, getData);
            return result;
        };
        return checks;
    });
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
                    collector.assume(field + " " + record[0]).push(
                            [ record[1], record[2] ]);
                });
            });
        });
        return collector.toValues();
    }, {
        controls : selectors,
        xaxis : {
            mode : "time"
        }
    });

}