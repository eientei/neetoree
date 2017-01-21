import React, { Component } from 'react';

class LogsPane extends Component {
    render() {
        const msgs = this.props.messages.content.map(m => {
            return (
                <div key={m.id}>
                    [{m.revisions[0].time}]
                    &lt; <strong>{m.revisions[0].authorName}</strong> &gt; {m.revisions[0].content ? m.revisions[0].content : (<em>(deleted)</em>)}
                </div>
            );
        });
        return (
            <div className={'messages'}>
                {msgs}
            </div>
        )
    }
}

export default LogsPane;