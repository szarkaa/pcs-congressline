(function() {
    'use strict';

    angular
        .module('pcsApp')
        .directive('optionalServiceMaxPersonValidation', OptionalServiceMaxPersonValidation);

    function OptionalServiceMaxPersonValidation() {
        var directive = {
            scope: {
                reserved: "="
            },
            require: 'ngModel',
            restrict: 'A',
            link: linkFunc
        };

        return directive;

        function linkFunc($scope, $element, $attrs, $ctrl) {
            function validation(value) {
                if ($scope.reserved > value) {
                    $ctrl.$setValidity('maxPerson', false);
                } else {
                    $ctrl.$setValidity('maxPerson', true);
                }
                return value;
            }

            $ctrl.$parsers.push(validation);
        }
    }

})();