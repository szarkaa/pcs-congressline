(function() {
    'use strict';

    angular.module('pcsApp')
        .factory('OptionalServiceApplicantFilter', OptionalServiceApplicantFilter);

    OptionalServiceApplicantFilter.$inject = ['$cookies', '$state'];

    function OptionalServiceApplicantFilter ($cookies, $state) {
        var optionalServiceApplicantFilter = null;

        return {
            setOptionalServiceApplicantFilter: function (filter) {
                if (filter) {
                    optionalServiceApplicantFilter = {
                        optionalServices: filter.optionalServices
                    };
                    $cookies.putObject('optional-service-applicant-filter', optionalServiceApplicantFilter);
                }
                else {
                    optionalServiceApplicantFilter = null;
                    $cookies.remove('optional-service-applicant-filter');
                }

            },
            getOptionalServiceApplicantFilter: function () {
                if (optionalServiceApplicantFilter == null) {
                    optionalServiceApplicantFilter = $cookies.getObject('optional-service-applicant-filter');
                    if (!optionalServiceApplicantFilter) {
                        return {
                            optionalServices: []
                        };
                    }
                }
                return optionalServiceApplicantFilter;
            }
        };
    }
})();
