(function() {
    'use strict';

    angular.module('pcsApp')
        .factory('CongressSelector', CongressSelector);

    CongressSelector.$inject = ['$state', '$window'];

    function CongressSelector ($state, $window) {
        var selectedCongress = null;

        return {
            setSelectedCongress: function (congress) {
                if (congress) {
                    selectedCongress = {
                        id: congress.id,
                        meetingCode: congress.meetingCode,
                        name: congress.name,
                        startDate: congress.startDate,
                        endDate: congress.endDate,
                        defaultCountry: congress.defaultCountry
                    };
                    $window.sessionStorage.setItem('selected-congress', angular.toJson(selectedCongress));
                }
                else {
                    selectedCongress = null;
                    $window.sessionStorage.removeItem('selected-congress');
                    $state.go('home');
                }

            },
            getSelectedCongress: function () {
                if (selectedCongress == null && $window.sessionStorage.getItem('selected-congress')) {
                    selectedCongress = angular.fromJson($window.sessionStorage.getItem('selected-congress'));
                }
                return selectedCongress;
            }
        };
    }
})();
