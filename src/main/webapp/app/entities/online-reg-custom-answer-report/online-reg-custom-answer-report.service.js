(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('OnlineRegCustomAnswerReport', OnlineRegCustomAnswerReport);

    OnlineRegCustomAnswerReport.$inject = ['$resource'];

    function OnlineRegCustomAnswerReport ($resource) {
        var resourceUrl =  'api/online-reg-custom-answer-report';

        return $resource(resourceUrl, {}, {
            'getCustomAnswers': { url: resourceUrl + '/:meetingCode/:currency', method: 'GET', isArray: true},
            'getAnswersByRegId': { url: resourceUrl + '/registration/:id', method: 'GET', isArray: true}
        });
    }
})();
