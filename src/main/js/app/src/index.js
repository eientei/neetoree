import React from 'react'
import { render } from 'react-dom'
import { Router, Route, browserHistory } from 'react-router'

import './index.css';

import App from './App';
import Logs from "./Logs";
import NoMatch from "./NoMatch";


render((
    <Router history={browserHistory}>
        <Route path="/" component={App}>
            <Route path="logs" component={Logs}>
                <Route path=":channel" component={Logs}>
                    <Route path=":date" component={Logs}/>
                </Route>
            </Route>
            <Route path="*" component={NoMatch}/>
        </Route>
    </Router>
), document.getElementById('root'));