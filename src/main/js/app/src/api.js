import rest from 'rest';
import mime from 'rest/interceptor/mime';
import errorCode from 'rest/interceptor/errorCode';

const client = rest
    .wrap(mime)
    .wrap(errorCode);

const root = 'http://discord.eientei.org:8095/api';

class Logs {
    static path = root + '/logs';
    static channels() {
        return client({path: this.path + '/channels'});
    }
    static dates(channel) {
        return client({path: this.path + '/dates/' + channel});
    }
    static messages(channel, date) {
        return client({path: this.path + '/messages/' + channel + '/' + date});
    }
}

class Api {
    static logs = Logs;
}

export default Api;