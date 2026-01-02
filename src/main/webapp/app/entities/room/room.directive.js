(function() {
    'use strict';

    angular
        .module('pcsApp')
        .directive('roomQuantityValidation', RoomQuantityValidation);

    function RoomQuantityValidation() {
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
                    $ctrl.$setValidity('quantity', false);
                } else {
                    $ctrl.$setValidity('quantity', true);
                }
                return value;
            }

            $ctrl.$parsers.push(validation);
        }
    }

})();
