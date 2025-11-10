interface GreetingProps {
  name: string;
}

export default function Greeting({ name }: GreetingProps) {
  return <p>Hello, {name}!</p>;
}