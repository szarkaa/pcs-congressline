(function() {
    'use strict';

    angular
        .module('pcsApp')
        .directive('dateLessThan', dateLessThan);

    dateLessThan.$inject = ['$filter'];

    function dateLessThan($filter) {
        var directive = {
            restrict: 'A',
            require: 'ngModel',
            link: linkFunc
        };

        return directive;

        function linkFunc(scope, element, attrs, ngModel) {
            var validate = function (modelValue) {
                var thresholdValue = attrs.dateLessThan;
/*
                console.log('thresholdValue:' + thresholdValue);
                console.log('modelValue:' + modelValue);
*/
                //todo valamiert "" jelek között érkezik meg ezért substr kell hogy meghamozzam, megnézni hogy miért van ez
                if (!modelValue || !thresholdValue) {
                    ngModel.$setValidity('dateLessThan', true);
                }
                else {
                    thresholdValue = "\"" === thresholdValue.substr(0, 1) && "\"" === thresholdValue.substr(thresholdValue.length - 1, thresholdValue.length - 2) ? thresholdValue.substr(1, thresholdValue.length - 2) : thresholdValue;
                    ngModel.$setValidity('dateLessThan',
                        +new Date($filter('date')(modelValue, 'yyyy-MM-dd')) >= +new Date($filter('date')(thresholdValue, 'yyyy-MM-dd')));
                }
                return modelValue;
            };

            //ngModel.$parsers.unshift(validate);
            ngModel.$parsers.push(validate);
            attrs.$observe('dateLessThan', function(thresholdValue){
                // Whenever the comparison model changes we'll re-validate
                return validate(ngModel.$modelValue);
            });
        }
    }
})();
