(function() {
    'use strict';

    angular
        .module('pcsApp')
        .directive('oosReservedValidation', OosReservedValidation);

    function OosReservedValidation() {
        var directive = {
            scope: {
                optionalservice: "="
            },
            require: 'ngModel',
            restrict: 'A',
            link: linkFunc
        };

        return directive;

        function linkFunc(scope, element, attr, mCtrl) {
            function validation(value) {
                if (scope.optionalservice) {
                    var reserved = scope.optionalservice.reserved + value;
                    if (scope.optionalservice.maxPerson < reserved) {
                        mCtrl.$setValidity('exceededMaxPerson', false);
                    } else {
                        mCtrl.$setValidity('exceededMaxPerson', true);
                    }
                    return value;
                }
                else {
                    mCtrl.$setValidity('exceededMaxPerson', true);
                }
            }

            mCtrl.$parsers.push(validation);
        }
    }
})();