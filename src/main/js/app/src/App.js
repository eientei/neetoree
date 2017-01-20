import React, { Component } from 'react';
import { Link } from 'react-router';
import './App.css';

class App extends Component {
    render() {
        return (
            <div className="App">
                <nav>
                    <ul>
                        <li><Link to={"/"}>Home</Link></li>
                        <li><Link to={"/logs"}>Logs</Link></li>
                    </ul>
                </nav>
                {this.props.children}
            </div>
        );
    }
}

export default App;
