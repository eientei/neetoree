import React, { Component } from 'react';
import { Link } from 'react-router';

class LogsChannels extends Component {
    render() {
        if (!this.props.channels) {
            return (
                <div>
                    Loading...
                </div>
            )
        }

        const channels = this.props.channels.filter(ch => ch != null).map(ch =>
            <Link className={'channel' + (this.props.current === ch ? ' active' : '')} to={"/logs/" + ch} key={ch}>#{ch}</Link>
        );

        return (
            <div>
                {channels}
            </div>
        )
    }
}

export default LogsChannels;