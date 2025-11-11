export default function BasicList() {
  const items = ["Apple", "Banana", "Orange"];

  return (
    <ul style={{ 
      listStyleType: "disc",   // makes bullets visible
      paddingLeft: "1.5rem",   // indent bullets properly
      margin: 0                 // remove extra top/bottom margin
    }}>
      {items.map(item => (
        <li key={item} style={{ marginBottom: "0.5rem" }}>
          {item}
        </li>
      ))}
    </ul>
  );
}
