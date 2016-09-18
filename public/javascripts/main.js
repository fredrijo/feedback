$(function () {
    ajaxCall();
});

$('.pull-down').each(function () {
    var $this = $(this);
    $this.css('margin-top', $this.parent().height() - $this.height())
});

var prevData;

var ajaxCall = function (data) {
    var ajaxCallBack = {
        success: onSuccess,
        error: onError
    };

    jsRoutes.controllers.Application.ajaxCall().ajax(ajaxCallBack);
};

var onSuccess = function (data) {
    document.getElementById("voteChart").innerHTML = data;
    drawChart(data);
};

var onError = function (error) {
    console.log(error);
};

setInterval(function () {
    ajaxCall()
}, 5000);

function countVotes(obj) {
    try {
        return obj['1'] + obj['2'] + obj['3'];
    } catch (err) {
        return 0;
    }
}

function sumVotes(obj) {
    try {
        return (obj['1'] * 1) + (obj['2'] * 2) + (obj['3'] * 3);
    } catch (err) {
        return 0;
    }
}

Array.prototype.max = function () {
    return Math.max.apply(null, this);
};

function maxVotes(obj) {
    try {
        return [obj['1'], obj['2'], obj['3']].max();
    } catch (err) {
        return 0;
    }
}

function drawChart(data) {
    if (countVotes(data) != countVotes(prevData)) {

        prevData = data;

        var ctx1 = document.getElementById("voteChart");
        ctx1.width = 200;
        ctx1.height = 200;

        new Chart(ctx1, {
            type: 'bar',
            legend: {
                display: false
            },
            data: {
                labels: labelArray,
                datasets: [{
                    data: [data['1'], data['2'], data['3']],
                    backgroundColor: [
                        'rgba(200, 0, 0, 0.6)',
                        'rgba(256, 200, 0, 0.6)',
                        'rgba(0, 200, 0, 0.6)'
                    ],
                    borderColor: [
                        'rgba(200, 0, 0, 1)',
                        'rgba(256, 200, 0, 1)',
                        'rgba(0, 200, 0, 1)'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                legend: {display: false},
                tooltips: {enabled: false},
                scales: {
                    yAxes: [{
                        ticks: {
                            beginAtZero: true
                        }
                    }]
                }
            }
        });

        var ctx2 = document.getElementById("avgChart");
        ctx2.width = ctx1.width;
        ctx2.height = ctx1.height;

        var avg = sumVotes(data) / countVotes(data);

        new Chart(ctx2, {
            type: 'bar',
            legend: {
                display: false
            },
            data: {
                labels: ["Idag", bestDate],
                datasets: [{
                    data: [avg - 1.0, bestScore - 1.0],
                    backgroundColor: [
                        'rgba(10, 187, 181, 0.6)',
                        'rgba(236, 160, 80, 0.6)'
                    ],
                    borderColor: [
                        'rgba(10, 187, 181, 1)',
                        'rgba(236, 160, 80, 1)'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                legend: {display: false},
                tooltips: {enabled: false},
                scales: {
                    yAxes: [
                        {

                            stacked: true,
                            ticks: {
                                fixedStepSize: 1,
                                min: 0,
                                max: 2,
                                beginAtZero: true,
                                callback: function (label, index, labels) {
                                    return labelArray[Math.abs(2 - index)];
                                }

                            },
                            position: "right"
                        }]
                }
            }
        });

    }

}
