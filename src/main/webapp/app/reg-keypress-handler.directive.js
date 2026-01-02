(function() {
    'use strict';

    angular
        .module('pcsApp')
        .directive('regKeypressHandler', RegKeypressHandler);

    RegKeypressHandler.$inject = ['$state'];

    function RegKeypressHandler($state) {
        var directive = {
            restrict: 'A',
            link: linkFunc
        };

        return directive;

        function linkFunc($scope, $element, $attrs, $ctrl) {
            var keys = [];

            document.onkeydown = function(e) {
                keys.push(e.key);
                if (keys.length > 9) {
                    keys.shift();
                }

                var regex = /REG-[0-9]{5}/g;
                var match = keys.join("").match(regex);
                if (match) {
                    var regId = parseInt(match[0].substring(4, match[0].length));
                    console.log('keypressed regId:', regId);
                    $state.go('registration', {registrationId: regId}, {reload: true});
                }
            }
        }
    }

})();