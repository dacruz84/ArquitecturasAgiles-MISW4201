import { sleep } from 'k6'
import http from 'k6/http'

/* 
export const options = {
    vus: 1,
    iterations: 100,
}
    */


export const options = {
    stages: [
        { duration: '10s', target: 10 },
        { duration: '30s', target: 20 },
        { duration: '10s', target: 0 },
    ],
}


export default function () {
    const url = 'http://localhost:9000/voting';
    const payload = JSON.stringify({
        items: 'P1, P3, P4, P6, P18, P5, P2, P11, P7, P10'
    });
    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };
    const expectedResponse = JSON.stringify({
        route: 'E,P1,P3,P4,P2,P5,P6,P7,P18,P11,P10,E'
    });
    const res = http.post(url, payload, params);
    if (res.body !== expectedResponse && res.status === 200) {
        throw new Error(`Unexpected response: ${res.body}`);
    }
}
