app.controller('multiMFTableController', ['$scope', '$http', '$interval', 'PagerService', 'PDGraphService',
    function($scope, $http, $interval, PagerService, PDGraphService) {
    initController();

    $scope.updateQuery = function() {
        $scope.dataNotDisplayed = true;
        $scope.pageNo = 0;
        $scope.count = 0;
        $scope.pageCount = 0;
        $scope.mfArray = [];
        $http.get('/query?symbol=' + $scope.querySymbol + '&pageNo=' + $scope.pageNo + '&maxEntries=' + $scope.maxEntries).
        success(function(data) {
            // Return objects are expected to be mutual funds
            $scope.mfArray = data;
            getCount();
            addCategoryData();
        }).
        error(function() {
            alert("failure");
        });
    }

    $scope.setPage = function (pageNo) {
        if (pageNo >= 0 && pageNo < $scope.pageCount) {
            getPage(pageNo);
        }
    }

    $scope.nextPage = function() {
        if ($scope.pageNo < $scope.pageCount-1) {
            $scope.dataNotDisplayed = true;
            var pageNo = $scope.pageNo + 1;
            getPage(pageNo);
        }
    }

    $scope.prevPage = function() {
        if ($scope.pageNo > 0) {
            $scope.dataNotDisplayed = true;
            var pageNo = $scope.pageNo - 1;
            getPage(pageNo);
        }
    }

    $scope.selectSymbol = function(symbol) {
        $scope.pdNotAvailable = false;
        $scope.noPDExists = false;
        $scope.loadingPD = true;
        PDGraphService.ClearGraph();
        $scope.selectedSymbol = symbol;
        // get price data starting at one year before current date, or 2018-01-01 if more recent
        var now = new Date();
        var oneYearBeforeNow = new Date();
        oneYearBeforeNow.setFullYear(now.getFullYear()-1);
        var janFirst2018 = new Date(2018, 0, 1);
        var beginningDate = oneYearBeforeNow.getTime() > janFirst2018.getTime() ?
                                          oneYearBeforeNow : janFirst2018;
        $scope.startDate = beginningDate.toISOString().split('T')[0];
        // query maximum # of times to try to get price quote with complete data
        var i = 1;
        var MAX_QUERIES = 20;
        var promise = $interval(function() {
            $http.get('/query/priceQuote?symbol=' + $scope.selectedSymbol +
            '&startDate=' + $scope.startDate).
            success(function(data) {
                    // expects to return price quote from symbol query
                    var priceQuote = data;
                    // finish & display graph if price quote intervals are received
                    // from 1st week of January to present
                    if ((priceQuote.intervals != null &&
                          priceQuote.intervals[0] != null &&
                          priceQuote.intervals[0].startDate.indexOf('2018-01-') >= 0 &&
                          Number(priceQuote.intervals[0].startDate.slice(-2)) <= 7)) {
                        $scope.loadingPD = false;
                        PDGraphService.CreateGraph(priceQuote, $scope.selectedSymbol);
                        $interval.cancel(promise);
                    }
                    // for every other request returned, update graph if intervals are available
                    else if (i % 2 == 0) {
                        if (priceQuote != null && priceQuote.intervals != null) {
                            if (priceQuote.intervals.length > 0) {
                                PDGraphService.CreateGraph(priceQuote, $scope.selectedSymbol);
                            }
                            /*else {
                                $scope.pdNotAvailable = true;
                            }*/
                        }
                    }
                    // cancel intervals and display status if price quote is completed,
                    // all requests are finished,
                    // or half of the requests are finished and no price data has been found
                    if (priceQuote.completed == true || i >= MAX_QUERIES) {
                        $scope.loadingPD = false;
                        if (priceQuote == null || priceQuote.intervals == null
                            || priceQuote.intervals.length == 0) {
                            $scope.noPDExists = true;
                        }
                        /*else {
                            $scope.pdNotAvailable = true;
                        }*/
                        $interval.cancel(promise);
                    }
                    else if (i >= MAX_QUERIES/4 &&
                             (priceQuote == null || priceQuote.intervals == null ||
                              priceQuote.intervals.length == 0)) {
                        $scope.loadingPD = false;
                        $scope.noPDExists = true;
                        $interval.cancel(promise);
                    }
                    i++;
                }).error(function() {
                    $scope.noPDExists = true;
                    $scope.loadingPD = false;
                });
            }, 1000, [MAX_QUERIES]);
    }

    function getCount() {
        $http.get('/count?symbol=' + $scope.querySymbol).
        success(function(data) {
            // Return object is expected to be count of all funds containing symbol
            $scope.count = data;
            $scope.pageCount = Math.ceil($scope.count/$scope.maxEntries);
            // get pager object from service
            $scope.pager = PagerService.GetPager($scope.count, $scope.pageCount, 0, $scope.maxEntries);
        }).
        error(function() {
            alert("failure to get count")
        });
    }

    function addCategoryData() {
        for (var i = 0; i < $scope.mfArray.length; i++) {
            var mf = $scope.mfArray[i];
            if (mf.fundSymbol != null &&
                (mf.fundName == null || mf.fundFamilyName == null ||
                    mf.categoryName == null || mf.categoryGroup == null)) {
                var symbol = mf.fundSymbol;
                var newMF = getCategoryData(symbol, mf);
                $scope.mfArray[i] = newMF;
            }
        }
        $scope.dataNotDisplayed = false;
    }

    function getCategoryData(symbol, mf) {
        $http.get('/getMFWithCategoryData?symbol=' + symbol).
        success(function(data) {
            if (data != null && data != "") {
                mf.categoryName = data.categoryName;
                mf.categoryGroup = data.categoryGroup;
            }
            else {
                console.error("ERROR: Could not gather category data for fund with symbol " + symbol);
            }
        }).
        error(function() {
            console.error("ERROR: No data collected from MF Parsing Service.");
        });
        return mf;
    }

    function getPage(pageNo) {
        // get current page of items
        $http.get('/query?symbol=' + $scope.querySymbol + '&pageNo=' + pageNo + '&maxEntries=' + $scope.maxEntries).
        success(function(data) {
            // Return objects are expected to be mutual funds
            if (data.length > 0) {
                $scope.pageNo = pageNo;
                $scope.mfArray = data;
                // get pager object from service
                $scope.pager = PagerService.GetPager($scope.count, $scope.pageCount, pageNo, $scope.maxEntries);
                addCategoryData();
            }
        }).
        error(function() {
            alert("failure in getPage");
        });
    }

    function initController() {
        $scope.dataNotDisplayed = true;
        $scope.pageNo = 0;
        $scope.pager = {};
        $scope.count = 0;
        $scope.querySymbol = "";
        $scope.selectedSymbol = "";
        $scope.mfArray = [];
        $scope.maxEntries = 10;
        $http.get('/query?symbol=&pageNo=0&maxEntries=' + $scope.maxEntries).
        success(function(data) {
            // Return objects are expected to be mutual funds
            $scope.mfArray = data;
            getCount();
            addCategoryData();
        }).
        error(function() {
            alert("failure in initController");
        });
    }

}]);

app.controller('pdTableController', ['$scope', 'PDGraphService', function($scope, PDGraphService) {
    $scope.pdiArray = [];
    $scope.noPDExists = false;
    $scope.$on('clearGraph', function(event, data) {
        $scope.pdiArray = [];
        $scope.noPDExists = false;
        $scope.data = [[],[],[]];
    });
    $scope.$on('createGraph', function (event, data) {
        $scope.pdiArray = [];
        $scope.noPDExists = false;
        var priceHistory = data.data;
        while (priceHistory.intervals[0] == null || priceHistory.intervals[0] === undefined) {
            priceHistory.intervals.shift();
        }
        $scope.symbol = data.symbol;
        $scope.pdiArray = priceHistory.intervals;
        if ($scope.pdiArray != null) {
            $scope.labels = [];
            $scope.data = [[],[],[]];
            for (var i = 0; i < $scope.pdiArray.length; i++) {
                $scope.labels.push($scope.pdiArray[i].startDate);
                $scope.data[0].push($scope.pdiArray[i].mean.toFixed(2));
                $scope.data[1].push($scope.pdiArray[i].high.toFixed(2));
                $scope.data[2].push($scope.pdiArray[i].low.toFixed(2));
            }
            $scope.series = ['Average', 'High', 'Low'];
            $scope.onClick = function (points, evt) {
                console.log(points, evt);
            };
            $scope.datasetOverride = [{ yAxisID: 'y-axis-1' }];
            $scope.options = {
                scales: {
                    yAxes: [
                        {
                            id: 'y-axis-1',
                            type: 'linear',
                            display: true,
                            position: 'left', 
                            scaleLabel: {
                                display: true, 
                                labelString: 'Price'
                            }
                        }
                    ], 
                    xAxes: [
                        {
                            scaleLabel: {
                                display: true, 
                                labelString: 'Date'
                            }
                        }
                    ]
                },
                responsive: false,
                maintainAspectRatio: false
            }
        }
        else {
            $scope.noPDExists = true;
        }
    });
}]);
