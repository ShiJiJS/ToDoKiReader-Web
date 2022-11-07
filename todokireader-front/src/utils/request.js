import axios from "axios";

var request = axios.create({
  baseURL: "http://localhost:8080",
});

export default request;
