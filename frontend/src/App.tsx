import './App.css'
import Header from "./components/Header";
import Greeting from "./components/Greeting";
import Counter from "./components/Counter";
import TextInput from "./components/TextInput";
import BasicList  from "./components/BasicList";
import UserList from "./components/UserList";
import TodoList from "./components/TodoList";
import SimpleForm from './components/SimpleForm';
import TextForm from './components/TextForm';
import MultiInputForm from './components/MultiInputForm';
import TodoForm from './components/TodoForm';
import TodoApp from './components/TodoApp';
import Box from './components/Box';
import Card from './components/Card';

function App() {
  return (
    <div>
      <Header />
    <Card>
      <MultiInputForm />
    </Card>

    </div>
    
  );
}

export default App;
