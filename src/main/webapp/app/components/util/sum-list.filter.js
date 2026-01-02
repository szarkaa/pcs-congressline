angular.module('pcsApp')
.filter('sumByKey', function () {
    return function(data, key) {
        var i, sum = 0;
        if (typeof data === 'undefined' || typeof key === 'undefined') {
            return sum;
        }

        for (i = 0; i < data.length; i++) {
            sum += (data[i][key]);
        }
        return sum;
    };
});
