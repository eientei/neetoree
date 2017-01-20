import React, { Component } from 'react';
import { Link } from 'react-router';
import LogsChannels from './logs/LogsChannels';
import LogsCalendar from './logs/LogsCalendar';
import LogsPane from './logs/LogsPane';
import NoMatch from './NoMatch';
import Api from './api';
import moment from 'moment';

import './Logs.css';

class Logs extends Component {
    constructor(props) {
        super(props);
        this.channelChange = this.channelChange.bind(this);
        this.dateChange = this.dateChange.bind(this);
        this.navigate = this.navigate.bind(this);

        this.state = {
            loaded: false
        };
    }

    navigate(e) {
        if (e.action === 'REPLACE') {
            return;
        }

        if (e.pathname && !e.pathname.startsWith("/logs")) {
            return;
        }

        Api.logs.channels()
            .then(res => {
                const channels = res.entity;
                this.setState({channels: channels});
                this.setState({channel: this.props.params.channel || channels[0]});
                if (this.state.channel == null && channels.length > 1) {
                    this.setState({channel: channels[1]});
                }
                return Api.logs.dates(this.state.channel);
            }, e => {
                console.error(e);
                this.setState({error: true});
            })
            .then(res => {
                const dates = res.entity;
                this.setState({dates: dates});
                this.setState({date: this.props.params.date || dates[dates.length-1].date});
                this.props.router.replace('/logs/' + this.state.channel + '/' + this.state.date);
                return Api.logs.messages(this.state.channel, this.state.date);
            }, e => {
                console.error(e);
                this.setState({error: true});
            })
            .then(res => {
                const messages = res.entity;
                this.setState({messages: messages, loaded: true});
            }, e => {
                console.error(e);
                this.setState({error: true});
            });
    }

    componentWillUnmount() {
        this.state.unlisten();
    }

    componentDidMount() {
        this.setState({unlisten: this.props.router.listen(this.navigate)});
        this.navigate({});
    }

    channelChange(value) {
        this.setState({channel: value});
    }

    dateChange(value) {
        this.setState({date: value});
        this.props.router.push('/logs/' + this.state.channel + '/' + value);
    }

    render() {
        if (this.state.messages && this.state.messages.totalElements === 0) {
            return (
                <NoMatch/>
            );
        }
        if (this.state.error) {
            return (
                <NoMatch/>
            );
        }
        if(!this.state.loaded) {
            return (
                <div/>
            )
        }

        const currentMoment = moment(this.state.date);
        let less = this.state.dates.filter(d => (moment(d.date).month() < currentMoment.month() && moment(d.date).year() === currentMoment.year()) || moment(d.date).year() < currentMoment.year());
        let more = this.state.dates.filter(d => (moment(d.date).month() > currentMoment.month() && moment(d.date).year() === currentMoment.year()) || moment(d.date).year() > currentMoment.year());

        let prev = null;
        let next = null;

        if (less.length > 0) {
            prev = (<Link className={'prev'} to={'/logs/' + this.state.channel + '/' + less[less.length-1].date}>Back</Link>);
        }

        if (more.length > 0) {
            next = (<Link className={'next'} to={'/logs/' + this.state.channel + '/' + more[0].date}>Next</Link>);
        }

        return (
            <div className={'logs'}>
                <LogsChannels current={this.state.channel} channels={this.state.channels} onChange={this.channelChange}/>
                <div className={'calnav'}>
                    <div className={'nav'}>
                        {prev && prev}
                        {this.state.date} @ #{this.state.channel}
                        {next && next}
                    </div>
                    <br/>
                    <LogsCalendar current={this.state.date} dates={this.state.dates} onChange={this.dateChange}/>
                </div>
                <LogsPane messages={this.state.messages}/>
            </div>
        );
    }
}

export default Logs;