import React, { Component } from 'react';
import moment from 'moment';

class LogsCalendar extends Component {
    findCount(d) {
        if (!this.props.dates) {
            return null;
        }

        const date = d.format('YYYY-MM-DD');
        const counts = this.props.dates.filter(e => e.date === date);
        if (counts.length > 0) {
            return counts[0].count;
        }
        return null;
    }

    stillOk(dayit, currentMoment) {
        return ((dayit.year() <= currentMoment.year() && ((dayit.month() < currentMoment.month()) || (dayit.month() === 11 && currentMoment.month() === 0))) || (dayit.month() === currentMoment.month()) || dayit.day() !== 1);
    }


    render() {
        const currentMoment = moment(this.props.current);

        let days = [];
        let dayit = moment(this.props.current, 'YYYY-MM-DD');
        dayit.date(1).subtract(dayit.isoWeekday()-1, 'days');
        for (let d = 0; this.stillOk(dayit, currentMoment); d++) {
            days.push({
                date: moment(dayit),
                margin: currentMoment.month() !== dayit.month(),
                count: this.findCount(dayit)
            });
            dayit = dayit.add(1, 'days');;
        }

        const els = days.map(d =>
            <div onClick={() => !d.margin && d.count && this.props.onChange(d.date.format('YYYY-MM-DD'))} className={'day' + (d.margin ? ' margin' : '') + (d.date.dayOfYear() === currentMoment.dayOfYear() ? ' current' : '')} key={d.date.format('YYYY-MM-DD')}>
                <div className={'date'}>{d.date.date()}</div>
                <div className={'count'}>{d.count}</div>
            </div>
        );

        return (
            <div className={'calendar'}>
                {els}
            </div>
        )
    }
}

export default LogsCalendar;