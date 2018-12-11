var app = angular.module('app', ['chart.js']);
app.factory('PagerService', function() {
    // service definition
    return {
        GetPager: function (count, pageCount, pageNo, maxEntries) {
            pageNo = pageNo || 0; // default to first page
            maxEntries = maxEntries || 10; // default page size is 10
            count = count || maxEntries; // default item count is one full page
            pageCount = pageCount || Math.ceil(count/maxEntries); // use page count if not null, otherwise calculate
            var startPage, endPage;
            if (pageCount <= 10) { // less than 10 total pages so show all
                startPage = 0;
                endPage = pageCount - 1;
            } else { // more than 10 total pages so calculate start and end pages
                if (pageNo <= 6) {
                    startPage = 0;
                    endPage = 9;
                } else if (pageNo + 4 >= pageCount - 1) {
                    startPage = pageCount - 10;
                    endPage = pageCount - 1;
                } else {
                    startPage = pageNo - 5;
                    endPage = pageNo + 4;
                }
            }
            var range = endPage - startPage + 1; // create an array of pages to ng-repeat in the pager control
            var pages = Array(range);
            for (var i = 0; i < range; i++) {
                pages[i] = i + startPage;
            }
            return {
                pages: pages // return pages required by the view
            };
        }
    }
});

app.factory('PDGraphService', function($rootScope) {
    return {
        ClearGraph: function() {
            $rootScope.$broadcast('clearGraph', {});
        },
        CreateGraph: function(priceQuote, symbol) {
            $rootScope.$broadcast('createGraph', {
                data: priceQuote, 
                symbol: symbol
            });
        },
        PDNotAvailableDirective: function() {
            $rootScope.dirOptions = {};
            $rootScope.dirOptions.pdNotAvailable();
        }, 
        NoPDExistsDirective: function(symbol) {
            $rootScope.dirOptions = {};
            $rootScope.dirOptions.noPDExists(symbol);
        }, 
    }
});

app.filter('highlight', function($sce) {
    return function(text, phrase) {
        if (phrase) text = text.replace(phrase, '<span class="bold_red">' + phrase +'</span>')
        return $sce.trustAsHtml(text)
    }
});
